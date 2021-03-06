/*
 * Expvp Minecraft game mode
 * Copyright (C) 2016-2017 Philipp Nowak (https://github.com/xxyy)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

static def findHipchatColor(build) {
    switch (build.result) {
        case "UNSTABLE":
            return "YELLOW"
        case "FAILED":
            return "RED"
        default:
            return "GREEN"
    }
}

pipeline {
    agent none // Don't block an agent while waiting for approval

    options {
        buildDiscarder(logRotator(numToKeepStr: '50'))
        disableConcurrentBuilds()
        skipDefaultCheckout()
    }

    tools {
        maven 'def-maven'
    }

    stages {
        stage('Maven Compile') {
            agent any
            steps {
                script {
                    leBuild = currentBuild
                }
                deleteDir()
                checkout scm
                sh 'mvn -B -V compile'
            }
        }

        stage('Maven Package') {
            agent any
            steps {
                sh 'mvn -B package'
            }
            post {
                always {
                    junit 'target/surefire-reports/*.xml'
                    archiveArtifacts 'target/expvp-*.jar'
                }
            }
        }

//        stage('Await QA Approval') {
//            input message: 'Deploy to Expvp QA?', submitter: 'xxyy'
//        }
    }
    post {
        always {
            script {
                node {
                    gitCommit = sh(returnStdout: true, script: 'git rev-parse HEAD').trim()
                }
            }
            hipchatSend color: findHipchatColor(leBuild),
                    message: "Built <a href='${currentBuild.absoluteUrl}'>Expvp " +
                            "#${currentBuild.number}</a> in " +
                            "${currentBuild.durationString.replaceAll(' and counting', '')}: " +
                            "${currentBuild.currentResult} [${gitCommit.take(6)}]"

        }
    }
}