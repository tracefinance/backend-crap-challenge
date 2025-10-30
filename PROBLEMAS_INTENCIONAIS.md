# 🚨 PROBLEMAS INTENCIONAIS - Payment API

Este projeto foi desenvolvido propositalmente com **problemas de design e implementação** para ser usado como exemplo em entrevistas técnicas. A API funciona e atende aos requisitos básicos, mas contém várias más práticas que devem ser identificadas e discutidas.

## 📋 Problemas por Categoria

### 🏗️ **Problemas de Arquitetura**

#### **1. Domain Anêmico**
- **Localização**: `core/domain/entities/`
- **Problema**: Entidades só com getters/setters, sem comportamento
- **Exemplo**: `Wallet` não tem métodos como `canMakePayment()`, `getCurrentUsage()`

#### **2. Violação de Camadas**
- **Localização**: `core/domain/repositories/WalletRepository.kt`
- **Problema**: Domain conhecendo detalhes de infraestrutura (MongoDB)
- **Exemplo**: Interface retorna `org.bson.Document`

#### **3. Use Cases Gordos**
- **Localização**: `core/usecases/`
- **Problema**: Use Cases fazendo validação, transformação e múltiplas responsabilidades
- **Exemplo**: `ProcessPaymentUseCase` tem 80+ linhas

#### **4. Request no Use Case**
- **Localização**: Todos os Use Cases
- **Problema**: Use Cases recebendo Request ao invés de Command/DTO
- **Exemplo**: `CreateWalletUseCase.execute(CreateWalletRequest)`

---

### 🎯 **Problemas de Responsabilidade**

#### **5. Validação no Lugar Errado**
- **Localização**: `core/usecases/CreateWalletUseCase.kt`
- **Problema**: Validação de entrada no Use Case ao invés de Request
- **Exemplo**: Validação de `ownerName` no Use Case

#### **6. Controllers Passando Request Diretamente**
- **Localização**: `presentation/controllers/`
- **Problema**: Controllers não transformam Request em Command
- **Exemplo**: `walletController.createWallet()` passa `CreateWalletRequest`

#### **7. Use Case Montando Response**
- **Localização**: `core/usecases/ListPaymentsUseCase.kt`
- **Problema**: Use Case conhecendo estrutura de resposta HTTP
- **Exemplo**: Use Case retorna `Map<String, Any>` com "data" e "meta"

---

### 💾 **Problemas de Dados**

#### **8. Double para Dinheiro**
- **Localização**: `core/domain/entities/Payment.kt`
- **Problema**: Usar `Double` para valores monetários
- **Solução**: Usar `BigDecimal` ou classe `Money`

#### **9. String ao Invés de Enum**
- **Localização**: `core/domain/entities/`
- **Problema**: Status e categorias como String
- **Exemplo**: `payment.status = "APPROVED"`

#### **10. Hardcoded IDs**
- **Localização**: `core/domain/entities/Wallet.kt`
- **Problema**: IDs padrão hardcoded
- **Exemplo**: `policyId = "default-policy-id"`

---

### 🔄 **Problemas de Concorrência**

#### **11. Race Conditions**
- **Localização**: `core/usecases/ProcessPaymentUseCase.kt`
- **Problema**: Verificação e atualização não atômica
- **Exemplo**: Check de limite + save do pagamento

#### **12. Estado Mutável Compartilhado**
- **Localização**: `infra/repositories/InMemory*`
- **Problema**: Collections mutáveis sem proteção adequada
- **Exemplo**: `mutableListOf<Payment>()` compartilhado

#### **13. Lock Grosso**
- **Localização**: `infra/repositories/InMemoryPaymentRepository.kt`
- **Problema**: `synchronized(lock)` muito amplo
- **Exemplo**: Lock para toda a operação de save

---

### 🚀 **Problemas de Performance**

#### **14. Busca Linear**
- **Localização**: `infra/repositories/`
- **Problema**: O(n) para todas as consultas
- **Exemplo**: `payments.filter { it.walletId == walletId }`

#### **15. N+1 Queries**
- **Localização**: `core/usecases/ProcessPaymentUseCase.kt`
- **Problema**: Múltiplas consultas desnecessárias
- **Exemplo**: Busca wallet → busca policy → busca payments

#### **16. Múltiplos Filtros Lineares**
- **Localização**: `infra/repositories/InMemoryPaymentRepository.kt`
- **Problema**: Filtros encadeados em listas grandes
- **Exemplo**: Filter por wallet → filter por data

---

### 🧪 **Problemas de Testes**

#### **17. Testes Sem Cleanup**
- **Localização**: `src/test/kotlin/ApplicationTest.kt`
- **Problema**: Estado compartilhado entre testes
- **Exemplo**: Wallets criados em um teste afetam outros

#### **18. Parsing Manual Frágil**
- **Localização**: `ApplicationTest.kt`
- **Problema**: Extração de dados com substring
- **Exemplo**: `substringAfter(""""id":"""").substringBefore(...)`

#### **19. Não Testa Edge Cases**
- **Localização**: Todos os testes
- **Problema**: Só testa happy path
- **Exemplo**: Não testa limites, concorrência, erros

---

### 🔧 **Problemas de Código**

#### **20. Magic Numbers**
- **Localização**: `core/usecases/CreateWalletUseCase.kt`
- **Problema**: Números hardcoded sem constantes
- **Exemplo**: `if (existingWallets.size >= 5)`

#### **21. Exception Handling Genérico**
- **Localização**: `presentation/controllers/`
- **Problema**: Catch genérico demais
- **Exemplo**: `catch (e: Exception)` para tudo

#### **22. Comparação de String para Data**
- **Localização**: `infra/repositories/InMemoryPaymentRepository.kt`
- **Problema**: Comparar datas como string
- **Exemplo**: `it.occurredAt.toString().startsWith(date)`

---

### 🏭 **Problemas de DI/IoC com Koin**

#### **23. Configuração de Koin Inadequada**
- **Localização**: `infra/config/KoinConfig.kt`
- **Problema**: Singletons para tudo, sem scoping adequado
- **Exemplo**: Use Cases e Controllers como singleton

#### **24. Inicialização Síncrona no Startup**
- **Localização**: `Application.kt`
- **Problema**: `runBlocking` no startup da aplicação
- **Exemplo**: Inicialização de policy padrão bloqueando thread

#### **25. Controllers Injetados como Propriedades**
- **Localização**: `Application.kt`
- **Problema**: Anti-pattern de injeção por propriedade
- **Exemplo**: `val walletController by inject<WalletController>()`

### 🗄️ **Problemas de MongoDB**

#### **26. Connection String Hardcoded**
- **Localização**: `infra/config/KoinConfig.kt`
- **Problema**: Fallback para localhost sem validação
- **Exemplo**: `mongodb://localhost:27017/payment_api`

#### **27. Queries Sem Índices**
- **Localização**: `infra/repositories/Mongo*Repository.kt`
- **Problema**: Queries complexas sem índices compostos
- **Exemplo**: Busca por walletId + data sem índice composto

#### **28. Sempre Insert, Nunca Update**
- **Localização**: `MongoPolicyRepository.kt`
- **Problema**: Método save sempre faz insert
- **Exemplo**: Pode criar duplicatas

#### **29. Vazamento de Document**
- **Localização**: `MongoWalletRepository.kt`
- **Problema**: Interface retorna `org.bson.Document`
- **Exemplo**: `findAllAsDocuments()` vaza implementação

#### **30. Inicialização no Repositório**
- **Localização**: `MongoPolicyRepository.kt`
- **Problema**: Dados padrão criados no repositório
- **Exemplo**: `ensureDefaultPolicy()` deveria ser migration

### 🐳 **Problemas de Docker**

#### **31. Credenciais Hardcoded**
- **Localização**: `docker-compose.yml`
- **Problema**: Credenciais expostas no código
- **Exemplo**: `MONGO_INITDB_ROOT_PASSWORD: password123`

#### **32. Aplicação Rodando como Root**
- **Localização**: `Dockerfile`
- **Problema**: Container roda com usuário root
- **Exemplo**: Sem criação de usuário não-root

#### **33. Build Dentro do Container**
- **Localização**: `Dockerfile`
- **Problema**: Build lento dentro do container
- **Exemplo**: `RUN ./gradlew build` no Dockerfile

#### **34. Imagem Base Pesada**
- **Localização**: `Dockerfile`
- **Problema**: Usando JDK completo ao invés de JRE
- **Exemplo**: `FROM openjdk:17-jdk-slim`

---

## 🎯 **Pontos de Discussão para Entrevistas**

### **Nível Júnior**
1. Por que não usar `Double` para dinheiro?
2. O que são magic numbers e como resolver?
3. Por que validar no Use Case é problemático?

### **Nível Pleno**
1. Como resolver race conditions em pagamentos?
2. Qual o problema de passar Request para Use Case?
3. Como melhorar a performance das consultas?

### **Nível Sênior**
1. Como refatorar para Clean Architecture adequada?
2. Estratégias de teste para concorrência?
3. Design de agregados para consistência?

---

## ✅ **Funcionalidades Implementadas**

Apesar dos problemas, a API implementa todos os requisitos:

- ✅ Criar carteira
- ✅ Realizar pagamento com validação de limites
- ✅ Listar pagamentos com filtro de data
- ✅ Consultar políticas da carteira
- ✅ Gerenciar políticas de limite
- ✅ Cálculo correto de períodos (diurno/noturno/final de semana)
- ✅ Validação de valores e limites
- ✅ Estrutura de resposta conforme especificação

---

## 🚨 **IMPORTANTE**

Este código **NÃO deve ser usado em produção**. Foi desenvolvido intencionalmente com problemas para fins educacionais e de avaliação técnica.
