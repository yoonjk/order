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
        sh '   echo "test Success"'
      }
    }
    stage('build image') {
      steps {
        script {
          echo "build image Success"
        }

      }
    }
    stage('deploy') {
      steps {
        sh 'echo "Deploy Success"'
      }
    }
    stage('System test') {
      steps {
        sh 'echo "System test Success"'
      }
    }
  }
}