## Hibernate Stateless Session tests

Some Hibernate Stateless Session tests

### NativeQueryTest

Example that shows:
- Starting stopping a transaction
- Performing createNativeMutationQuery to insert data without using JPA or Entities.
- Performing createNativeQuery to select data without using JPA or Entities.
- Using setTupleTransformer to convert to a DTO object that is not managed by Hibernate.
- Using setTupleTransformer to convert to a Record object that is not managed by Hibernate.