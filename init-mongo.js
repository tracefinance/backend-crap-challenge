// Problema 1: Script de inicialização sem validações
db = db.getSiblingDB('payment_api');

// Problema 2: Criando usuário com privilégios excessivos
db.createUser({
  user: 'payment_user',
  pwd: 'payment_pass',
  roles: [
    {
      role: 'readWrite',
      db: 'payment_api'
    }
  ]
});

// Problema 3: Índices inadequados ou faltando
db.wallets.createIndex({ "ownerName": 1 });
db.payments.createIndex({ "walletId": 1 });
// Problema 4: Faltam índices compostos para queries complexas
// db.payments.createIndex({ "walletId": 1, "occurredAt": 1 }); // Comentado intencionalmente

// Problema 5: Dados de exemplo em produção
db.policies.insertOne({
  _id: "default-policy-id",
  id: "default-policy-id",
  name: "DEFAULT_VALUE_LIMIT",
  category: "VALUE_LIMIT",
  maxPerPayment: 1000.0,
  daytimeDailyLimit: 4000.0,
  nighttimeDailyLimit: 1000.0,
  weekendDailyLimit: 1000.0,
  createdAt: new Date(),
  updatedAt: new Date()
});

print('Database initialized with problems!');
