#!/usr/bin/env groovy

@Library("jenkins-shared-library")

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
                    dockerBuildImage 'diaa96/shopme:backend-1.0', './ShopmeWebParent/ShopmeBackend/'
                    dockerBuildImage 'diaa96/shopme:frontend-1.0', './ShopmeWebParent/ShopmeFrontend/'
                    dockerLogin()
                    dockerPush 'diaa96/shopme:backend-1.0'
                    dockerPush 'diaa96/shopme:frontend-1.0'
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
    }
}