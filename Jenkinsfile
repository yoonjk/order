pipeline {
  agent any
  stages {
    stage('build') {
      steps {
        git(url: 'https://github.com/yoonjk/order.git', branch: 'cloud-native', credentialsId: 'satan7048@', changelog: true, poll: true)
      }
    }
    stage('test') {
      steps {
        sh '''    stage(\'Build\') {
      steps {
        tool \'maven\'
      }
    }'''
        }
      }
    }
  }