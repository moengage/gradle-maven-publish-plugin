package com.moengage.internal

import com.moengage.internal.model.NexusArtifactTransitionState
import com.moengage.internal.repository.NexusRepository
import com.moengage.internal.repository.Repository
import com.moengage.internal.utils.LogLevel
import com.moengage.internal.utils.log
import java.io.IOException

private const val transitionWaitTime = 1L * 60 * 1000 // 1 minute

/**
 * Repository handler for the OSS Portal
 *
 * @author Abhishek Kumar
 * @since 1.0.0
 */
internal class NexusRepositoryHandler(private val repository: NexusRepository) : Repository {

    private val tag = "${BASE_TAG}_NexusRepositoryHandler"

    /**
     * Pre-release process for the repository. Should be called before the repository is staged
     * @since 1.0.0
     */
    fun getStagedRepositoryId(groupName: String, artifactName: String, version: String): String {
        log(message = "$tag getStagedRepositoryId(): Started")
        val stagedRepositoryId = repository.createRepository("$groupName:$artifactName:$version")
            ?: throw IOException("Cannot create repository for $groupName:$artifactName:$version")
        return stagedRepositoryId
    }

    /**
     * Close and the release the staged repository
     * @since 1.0.0
     */
    fun closeAndRelease(stagedRepositoryId: String) {
        log(message = "$tag closeAndRelease(): Started for $stagedRepositoryId")
        val repository = repository.getRepositoryData(stagedRepositoryId) ?: run {
            log(message = "$tag closeAndRelease(): no open repository found for $stagedRepositoryId")
            return
        }

        if (!moveToNextStepIfRequired(repository.repositoryId)) {
            log(LogLevel.NOTICE, message = "$tag closeAndRelease(): repository release / dropped for $stagedRepositoryId")
        } else {
            log(message = "$tag closeAndRelease(): waiting for transition")
            Thread.sleep(transitionWaitTime)
            closeAndRelease(repository.repositoryId)
        }
    }

    /**
     * Return true if next state is possible else false
     * @since 1.0.0
     */
    private fun moveToNextStepIfRequired(stagedRepositoryId: String): Boolean {
        log(message = "$tag moveToNextStepIfRequired(): ")
        val repositoryData = repository.getRepositoryData(stagedRepositoryId) ?: run {
            log(message = "$tag moveToNextStepIfRequired(): no open repository found for $stagedRepositoryId")
            return false
        }

        if (repositoryData.transitioning) {
            log(message = "$tag moveToNextStepIfRequired(): waiting for transition")
            Thread.sleep(transitionWaitTime)
            return moveToNextStepIfRequired(stagedRepositoryId)
        }

        val currentState = NexusArtifactTransitionState.valueOf(repositoryData.type.uppercase())
        log(message = "$tag moveToNextStepIfRequired(): current state = $currentState")
        return when (currentState) {
            NexusArtifactTransitionState.OPEN -> {
                log(message = "$tag moveToNextStepIfRequired(): closing repository")
                repository.closeRepository(stagedRepositoryId)
                true
            }

            NexusArtifactTransitionState.CLOSED -> {
                log(message = "$tag moveToNextStepIfRequired(): promoting repository")
                repository.promoteRepository(stagedRepositoryId)
                true
            }

            NexusArtifactTransitionState.RELEASED -> {
                log(message = "$tag moveToNextStepIfRequired(): dropping repository")
                repository.dropRepository(stagedRepositoryId)
                false
            }
        }
    }
}
