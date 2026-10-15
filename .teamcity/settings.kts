import jetbrains.buildServer.configs.kotlin.*
import jetbrains.buildServer.configs.kotlin.buildSteps.script
import jetbrains.buildServer.configs.kotlin.triggers.vcs

version = "2025.03"

project {
    description = "https://github.com/octopusden/octopus-test-tc"

    buildType(BuildValidation)
    buildType(WlValidation)

    params {
        param("OCTOPUS_MODULE_NAME", "octopus-test-tc")
    }
}

object BuildValidation : BuildType({
    id("BuildValidation")
    name = "[1.0] Build Validation [AUTO]"

    vcs {
        root(AbsoluteId("Octopus_OctopusComponents_OctopusGithubVcsRoot"))
    }

    steps {
        script {
            id = "BuildPlaceholder"
            name = "Build (placeholder)"
            scriptContent = "echo 'No build is defined for this repository yet.'"
        }
        step {
            id = "PostStatusSuccess"
            name = "Post status: success (build/integration-a)"
            type = "PostGithubCommitStatus"
            executionMode = BuildStep.ExecutionMode.RUN_ON_SUCCESS
            param("COMMIT_STATUS_STATE", "success")
            param("COMMIT_STATUS_CONTEXT", "build/integration-a")
            param("COMMIT_STATUS_DESCRIPTION", "")
            param("COMMIT_SHA", "%build.vcs.number%")
            param("GITHUB_OWNER", "%OCTOPUS_GITHUB_USERNAME%")
            param("GITHUB_REPO", "%OCTOPUS_MODULE_NAME%")
            param("GITHUB_API_URL", "%GITHUB_API_URL%")
        }
        step {
            id = "PostStatusFailure"
            name = "Post status: failure (build/integration-a)"
            type = "PostGithubCommitStatus"
            executionMode = BuildStep.ExecutionMode.RUN_ONLY_ON_FAILURE
            param("COMMIT_STATUS_STATE", "failure")
            param("COMMIT_STATUS_CONTEXT", "build/integration-a")
            param("COMMIT_STATUS_DESCRIPTION", "")
            param("COMMIT_SHA", "%build.vcs.number%")
            param("GITHUB_OWNER", "%OCTOPUS_GITHUB_USERNAME%")
            param("GITHUB_REPO", "%OCTOPUS_MODULE_NAME%")
            param("GITHUB_API_URL", "%GITHUB_API_URL%")
        }
    }

    triggers {
        vcs {
            branchFilter = "+:*"
        }
    }
})

object WlValidation : BuildType({
    templates(AbsoluteId("OctopusWlValidator"))
    id("WlValidation")
    name = "WL Validation"

    params {
        param("WL_COMMIT_STATUS_CONTEXT", "security/wl-a")
    }
})
