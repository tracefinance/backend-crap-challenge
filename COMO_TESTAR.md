# 🧪 Como Testar a Payment API

## 🚀 Executando a Aplicação

### Opção 1: Docker Compose (Recomendado)
```bash
# Subir aplicação + MongoDB
docker-compose up --build

# A API estará disponível em http://localhost:8080
```

### Opção 2: Desenvolvimento Local
```bash
# 1. Subir apenas MongoDB
docker-compose up mongodb -d

# 2. Executar aplicação
./gradlew run
```

## 🔧 Testando os Endpoints

### 1. Health Check
```bash
curl http://localhost:8080/health
```

### 2. Criar Carteira
```bash
curl -X POST http://localhost:8080/wallets \
  -H "Content-Type: application/json" \
  -d '{"ownerName": "João Silva"}'
```

### 3. Listar Políticas da Carteira
```bash
# Use o ID retornado na criação da carteira
curl http://localhost:8080/wallets/{WALLET_ID}/policies
```

### 4. Realizar Pagamento
```bash
curl -X POST http://localhost:8080/wallets/{WALLET_ID}/payments \
  -H "Content-Type: application/json" \
  -d '{
    "amount": 500.0,
    "occurredAt": "2024-01-15T10:30:00Z"
  }'
```

### 5. Listar Pagamentos
```bash
# Todos os pagamentos
curl http://localhost:8080/wallets/{WALLET_ID}/payments

# Com filtro de data
curl "http://localhost:8080/wallets/{WALLET_ID}/payments?startDate=2024-01-01T00:00:00Z&endDate=2024-01-31T23:59:59Z"
```

### 6. Criar Nova Política
```bash
curl -X POST http://localhost:8080/policies \
  -H "Content-Type: application/json" \
  -d '{
    "name": "POLICY_PREMIUM",
    "category": "VALUE_LIMIT",
    "maxPerPayment": 2000,
    "daytimeDailyLimit": 8000,
    "nighttimeDailyLimit": 2000,
    "weekendDailyLimit": 3000
  }'
```

### 7. Listar Políticas
```bash
curl http://localhost:8080/policies
```

### 8. Associar Política à Carteira
```bash
curl -X PUT http://localhost:8080/wallets/{WALLET_ID}/policy \
  -H "Content-Type: application/json" \
  -d '{"policyId": "{POLICY_ID}"}'
```

## 🧪 Cenários de Teste para Identificar Problemas

### Teste de Limites
```bash
# 1. Criar carteira
WALLET_ID=$(curl -s -X POST http://localhost:8080/wallets \
  -H "Content-Type: application/json" \
  -d '{"ownerName": "Test User"}' | jq -r '.id')

# 2. Fazer pagamento de R$ 1000 (máximo permitido)
curl -X POST http://localhost:8080/wallets/$WALLET_ID/payments \
  -H "Content-Type: application/json" \
  -d '{"amount": 1000.0, "occurredAt": "2024-01-15T10:00:00Z"}'

# 3. Tentar fazer outro pagamento (deve falhar por limite diurno)
curl -X POST http://localhost:8080/wallets/$WALLET_ID/payments \
  -H "Content-Type: application/json" \
  -d '{"amount": 4000.0, "occurredAt": "2024-01-15T11:00:00Z"}'
```

### Teste de Períodos
```bash
# Pagamento diurno (6:00-18:00)
curl -X POST http://localhost:8080/wallets/$WALLET_ID/payments \
  -H "Content-Type: application/json" \
  -d '{"amount": 500.0, "occurredAt": "2024-01-15T14:00:00Z"}'

# Pagamento noturno (18:00-6:00)
curl -X POST http://localhost:8080/wallets/$WALLET_ID/payments \
  -H "Content-Type: application/json" \
  -d '{"amount": 500.0, "occurredAt": "2024-01-15T20:00:00Z"}'

# Pagamento no fim de semana
curl -X POST http://localhost:8080/wallets/$WALLET_ID/payments \
  -H "Content-Type: application/json" \
  -d '{"amount": 500.0, "occurredAt": "2024-01-13T14:00:00Z"}'
```

### Teste de Validações
```bash
# Valor inválido (maior que R$ 1000)
curl -X POST http://localhost:8080/wallets/$WALLET_ID/payments \
  -H "Content-Type: application/json" \
  -d '{"amount": 1500.0, "occurredAt": "2024-01-15T10:00:00Z"}'

# Valor negativo
curl -X POST http://localhost:8080/wallets/$WALLET_ID/payments \
  -H "Content-Type: application/json" \
  -d '{"amount": -100.0, "occurredAt": "2024-01-15T10:00:00Z"}'

# Data inválida
curl -X POST http://localhost:8080/wallets/$WALLET_ID/payments \
  -H "Content-Type: application/json" \
  -d '{"amount": 100.0, "occurredAt": "data-invalida"}'
```

## 🗄️ Acessando o MongoDB

```bash
# Conectar ao MongoDB
docker exec -it payment-api-mongo mongosh payment_api

# Comandos úteis
db.wallets.find()
db.payments.find()
db.policies.find()
```

## 🚨 Problemas Esperados

Durante os testes, você deve observar:

1. **Performance**: Consultas lentas sem índices
2. **Concorrência**: Race conditions em pagamentos simultâneos
3. **Validação**: Mensagens de erro inadequadas
4. **Arquitetura**: Vazamento de detalhes de implementação
5. **Segurança**: Credenciais expostas no docker-compose

## 🔍 Logs e Debug

```bash
# Ver logs da aplicação
docker-compose logs payment-api -f

# Ver logs do MongoDB
docker-compose logs mongodb -f
```
