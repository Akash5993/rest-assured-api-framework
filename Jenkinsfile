// Jenkins pipeline for the API test suite ("pipeline as code").
// Simpler than the UI pipeline: API tests need no browser, so no headless mode and no screenshots.
pipeline {
    agent any

    // A laptop Jenkins can't receive GitHub webhooks, so it checks GitHub for new commits every ~5 minutes.
    triggers {
        pollSCM('H/5 * * * *')
    }

    options {
        timeout(time: 15, unit: 'MINUTES')
        buildDiscarder(logRotator(numToKeepStr: '20'))
    }

    stages {
        stage('Run API tests') {
            steps {
                script {
                    // test.failure.ignore: failed tests mark the build UNSTABLE (yellow) via the junit step,
                    // instead of a hard FAILURE (red), so the test report is always published.
                    def mvnCommand = 'mvn -B clean test -Dmaven.test.failure.ignore=true'
                    if (isUnix()) {
                        sh mvnCommand
                    } else {
                        bat mvnCommand
                    }
                }
            }
        }
    }

    post {
        always {
            // TEST-*.xml only: testng-results.xml in the same folder is TestNG's own format, not JUnit XML
            junit testResults: 'target/surefire-reports/TEST-*.xml', allowEmptyResults: true
        }
    }
}
