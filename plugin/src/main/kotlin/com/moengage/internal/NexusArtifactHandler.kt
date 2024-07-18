package com.moengage.internal

import com.moengage.internal.model.NexusTransitionState
import com.moengage.internal.repository.NexusRepository
import java.io.IOException

private const val transitionWaitTime = 1L * 60 * 1000

internal class NexusArtifactHandler(private val repository: NexusRepository) {

    fun preRelease(groupName: String, artifactName: String, version: String): String {
        val stagedRepositoryId = repository.createRepository("$groupName:$artifactName:$version")
            ?: throw IOException("Cannot create repository for $groupName:$artifactName:$version")
        return stagedRepositoryId
    }

    fun closeAndRelease(stagedRepositoryId: String) {
        println("::info::will close and release $stagedRepositoryId")
        val filteredRepository = repository.getStagedRepositories().filter {
            it.repositoryId == stagedRepositoryId
        }
        if (filteredRepository.isEmpty()) {
            println("::info::no repository with $stagedRepositoryId is staged")
            return
        }

        println("::info::moving $stagedRepositoryId to next state")
        if (moveToNextStepIfRequired(filteredRepository[0].repositoryId, NexusTransitionState.valueOf(filteredRepository[0].type))) {
            println("::info::release completed for $stagedRepositoryId")
        } else {
            println("::info::moving completed for $stagedRepositoryId, will try to again move to next step")
            Thread.sleep(transitionWaitTime)
            closeAndRelease(filteredRepository[0].repositoryId)
        }
    }

    private fun moveToNextStepIfRequired(stagedRepositoryId: String, currentState: NexusTransitionState): Boolean {
        val repositoryData = repository.getRepositoryData(stagedRepositoryId) ?: run {
            println("::info::repository $stagedRepositoryId also dropped")
            return false
        }
        if (repositoryData.transitioning) {
            println("::debug::repository is in transitioning phase, waiting for transition to be completed")
            Thread.sleep(transitionWaitTime)
            println("::debug::waiting time over, trying to move from $currentState to next state")
            return moveToNextStepIfRequired(stagedRepositoryId, currentState)
        }

        return when (currentState) {
            NexusTransitionState.OPEN -> {
                println("::debug::trying to move to from $currentState to closed")
                repository.closeRepository(stagedRepositoryId)
                println("::debug::repository with $stagedRepositoryId closed")
                false
            }

            NexusTransitionState.RELEASED -> {
                println("::debug::trying to move to from $currentState to dropped state")
                repository.dropRepository(stagedRepositoryId)
                println("::debug::repository with $stagedRepositoryId dropped state")
                true
            }

            NexusTransitionState.OTHER -> {
                println("::debug::trying to move to from $currentState to next state")
                repository.promoteRepository(stagedRepositoryId)
                println("::debug::repository with $stagedRepositoryId next state")
                false
            }
        }
    }
}
