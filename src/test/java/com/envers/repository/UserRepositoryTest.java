package com.envers.repository;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.history.Revision;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.util.Assert;

import com.envers.config.PersistenceConfiguration;
import com.envers.entity.User;

/**
 * @author abidk
 *
 */
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
