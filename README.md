Spring Data JPA Envers Integration POC
======================================

In this POC I have shown how we can use or integrate  spring-data-envers with existing jpa repositories.

First we need to add dependency for spring-data-envers in our pom.

```xml
<dependency>
	<groupId>org.springframework.data</groupId>
	<artifactId>spring-data-envers</artifactId>
	<version>0.2.0.RELEASE</version>
</dependency>
```

Secondly, for java based configuration, we  need update `repositoryFactoryBeanClass`  attribute of `@EnableJpaRepositories` with `org.springframework.data.envers.repository.support.EnversRevisionRepositoryFactoryBean.class`

```java
@EnableJpaRepositories(basePackages = "com.envers.repository", repositoryFactoryBeanClass = EnversRevisionRepositoryFactoryBean.class)
```

For XML based configuration, we  have to update `factory-class` attribute of `jpa:repositories`.

```xml
<jpa:repositories base-package="com.envers.repository"
		factory-class="org.springframework.data.envers.repository.support.EnversRevisionRepositoryFactoryBean" />
```

Third, our existing repository should extends  `org.springframework.data.repository.history.RevisionRepository<T, ID extends Serializable, N extends Number & Comparable<N>>`

```java
@Repository
public interface UserRepository extends RevisionRepository<User, Long, Integer> , JpaRepository<User, Long>{

}
```

So, spring-data-envers is now  configured. Let's test it. We will write a small test case for this purpose.


```java
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { PersistenceConfiguration.class }, loader = AnnotationConfigContextLoader.class)
@TransactionConfiguration(defaultRollback = true)
public class UserRepositoryTest {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private JpaTransactionManager transactionManager;
	
	@Before
	public void before(){
		userRepository.deleteAll();
	}
	
	@After
	public void after(){
		userRepository.deleteAll();
	}

	@Test
	public void testRevision() {

		TransactionTemplate transactionTemplate = new TransactionTemplate(
				transactionManager);
		
		
		final User user  = transactionTemplate.execute(new TransactionCallback<User>() {

			@Override
			public User doInTransaction(TransactionStatus status) {
				return userRepository.saveAndFlush(getUser());
			}
			
		});
		
		

		transactionTemplate.execute(new TransactionCallbackWithoutResult() {

			@Override
			protected void doInTransactionWithoutResult(TransactionStatus status) {
				Revision<Integer, User> revison = userRepository
						.findLastChangeRevision(user.getId());
				Assert.notNull(revison);
				Assert.notNull(revison.getEntity());

			}
		});

	}

	public User getUser() {
		User user = new User();
		user.setFirstName("abid");
		user.setLastName("Khan");
		user.setStatus("NEW");
		return user;

	}

}
```


