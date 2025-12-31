pipeline {
  agent any

  options {
    timestamps()
    disableConcurrentBuilds()
    skipDefaultCheckout(true)
    buildDiscarder(logRotator(numToKeepStr: '20'))
  }

  environment {
    MAVEN_OPTS = '-Dfile.encoding=UTF-8'
  }

  stages {
    stage('1-Checkout (GitHub)') {
      steps {
        checkout scm
      }
    }

    stage('2-Build') {
      steps {
        powershell(script: '''
          $ErrorActionPreference = "Stop"

          Write-Host "== Backend build (skip tests) =="
          Push-Location "demo"
          try {
            & .\\mvnw.cmd -B -DskipTests clean package
          } finally {
            Pop-Location
          }

          Write-Host "== Frontend build =="
          Push-Location "frontend"
          try {
            if (Test-Path "package-lock.json") {
              npm ci
            } else {
              npm install
            }
            npm run build
          } finally {
            Pop-Location
          }
        ''')
      }
    }

    stage('3-Unit Tests (Birim)') {
      steps {
        powershell(script: '''
          $ErrorActionPreference = "Stop"
          Push-Location "demo"
          try {
            & .\\mvnw.cmd -B "-Dtest=com.example.BirimTestleri.*Test" test
          } finally {
            Pop-Location
          }
        ''')
      }
      post {
        always {
          junit allowEmptyResults: true, testResults: 'demo/target/surefire-reports/*.xml'
        }
      }
    }

    stage('4-Integration Tests (Entegrasyon)') {
      steps {
        powershell(script: '''
          $ErrorActionPreference = "Stop"
          Push-Location "demo"
          try {
            & .\\mvnw.cmd -B "-Dtest=com.example.EntegrasyonTestleri.*IntegrationTest" test
          } finally {
            Pop-Location
          }
        ''')
      }
      post {
        always {
          junit allowEmptyResults: true, testResults: 'demo/target/surefire-reports/*.xml'
        }
      }
    }

    stage('5-Run System on Docker') {
      steps {
        powershell(script: '''
          $ErrorActionPreference = "Stop"

          Write-Host "== docker compose up (idempotent) =="
          & docker compose -f docker-compose.yml up -d --build --wait
          if ($LASTEXITCODE -ne 0) {
            Write-Host "docker compose --wait not supported or failed; retrying without --wait"
            & docker compose -f docker-compose.yml up -d --build
            if ($LASTEXITCODE -ne 0) { throw "docker compose up failed (exit=$LASTEXITCODE)" }
          }

          Write-Host "== basic reachability checks (frontend port only) =="
          function Test-TcpPort([string]$HostName, [int]$Port, [int]$TimeoutMs = 1500) {
            $client = New-Object System.Net.Sockets.TcpClient
            try {
              $iar = $client.BeginConnect($HostName, $Port, $null, $null)
              if (-not $iar.AsyncWaitHandle.WaitOne($TimeoutMs, $false)) { return $false }
              $client.EndConnect($iar)
              return $true
            } catch {
              return $false
            } finally {
              try { $client.Close() } catch { }
            }
          }

          $max = 45
          for ($i = 1; $i -le $max; $i++) {
            $feOk = Test-TcpPort -HostName 'localhost' -Port 1313
            if ($feOk) {
              Write-Host "Frontend port is reachable (1313)."
              break
            }
            if ($i -eq $max) {
              throw "Frontend not reachable on port 1313 after waiting."
            }
            Start-Sleep -Seconds 2
          }
        ''')
      }
    }

    stage('6-Selenium Tests (UI)') {
      steps {
        powershell(script: '''
          $ErrorActionPreference = "Stop"
          # Tüm Selenium testlerini çalıştırır (BagimsizTestler + Senaryo*FlowTest).
          & .\\demo\\mvnw.cmd -B -f seleniumtestleri\\pom.xml test
        ''')
      }
      post {
        always {
          junit allowEmptyResults: true, testResults: 'seleniumtestleri/target/surefire-reports/*.xml'
        }
      }
    }
  }

}
