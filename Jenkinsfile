#!/usr/bin/env groovy

library identifier: 'jenkins-shared-library@master', retriever: modernSCM(
        [$class       : 'GitSCMSource',
         remote       : 'https://github.com/DiaaRamadan/jenkins-shared-library.git',
         credentialsId: 'github-creds'
        ]
)

def gv
pipeline {

    agent any

    tools {
        maven 'maven-3.9'
    }

    stages {

       stage("init") {
            steps {
                script {

                    gv = load "script.groovy"
                }
            }
       }

        stage("Increment version"){
            steps{
                script{
                    echo "Increment app version..."
                    sh 'mvn build-helper:parse-version versions:set \
                        -DnewVersion=\\\${parsedVersion.majorVersion}.\\\${parsedVersion.minorVersion}.\\\${parsedVersion.nextIncrementalVersion} \
                        versions:commit'

                    def pomContent = readFile('pom.xml')  =~ '<version>(.+)</version>'
                    def version = pomContent[0][1]

                    env.IMAGE_NAME = "$version-$BUILD_NUMBER"

                    echo "==== new image value ${IMAGE_NAME} ===="
                }
            }
        }

        stage("build jar") {

            steps {
                script {
                    buildJar()
                }
            }

        }

        stage("build and push image") {

            steps {
                script {
                    buildImage "diaa96/shopme:backend-${IMAGE_NAME}", './ShopmeWebParent/ShopmeBackend/'
                    buildImage "diaa96/shopme:frontend-${IMAGE_NAME}", './ShopmeWebParent/ShopmeFrontend/'
                    dockerLogin()
                    dockerPush "diaa96/shopme:backend-${IMAGE_NAME}"
                    dockerPush "diaa96/shopme:frontend-${IMAGE_NAME}"
                }
            }

        }

        stage("deploy") {

            steps {
                script {
                    gv.deployApp()
                }
            }
        }

        stage("Commit version update"){
            steps{
               script{
                    withCredentials([script.usernamePassword(credentialsId: 'github-creds', usernameVariable: 'USER', passwordVariable: 'PASS')]) {
                    sh 'git --global config.email "jenkins@example.com"'
                    sh 'git --global config.name "jenkins"'

                    sh 'git status'
                    sh 'git branch'
                    sh 'git config --list'

                    sh "git remote set-url origin https://${USER}:${PASS}@github.com/DiaaRamadan/shopme.git"
                    sh 'git add .'
                    sh 'git commit -m "ci:Version bump"'
                    sh 'git push origin head:'
                    sh 'git push origin HEAD:jenkins-shared-library'
                    
                }
               }
            }
        }
    }
}