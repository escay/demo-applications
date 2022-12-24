## Jakarta Persistence Stateless Session tests

Some Stateless Session tests, however there is no Stateless Session in 
Jakarta Persistence available at this moment. See [#374](https://github.com/jakartaee/persistence/issues/374)
So using the EntityManager instead to insert and select non Entity objects.

### NativeQueryTest

Example that shows:
- Starting stopping a transaction using @Transactional
- Performing createNativeQuery to insert data without using JPA or Entities.
- Performing createNativeQuery to select data without using JPA or Entities.
- Using stream and map to convert to a DTO object that is not managed by the EntityManager.
- Using stream and map to convert to a Record that is not managed by the EntityManager.
