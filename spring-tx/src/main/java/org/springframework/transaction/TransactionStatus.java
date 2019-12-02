/*
 * Copyright 2002-2016 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.transaction;

import java.io.Flushable;

/**
 * Representation of the status of a transaction.
 *
 * <p>Transactional code can use this to retrieve status information,
 * and to programmatically request a rollback (instead of throwing
 * an exception that causes an implicit rollback).
 *
 * <p>Derives from the SavepointManager interface to provide access
 * to savepoint management facilities. Note that savepoint management
 * is only available if supported by the underlying transaction manager.
 *
 * <p>
 *     表示事务状态。
 * <p>
 *     可以使用它来检查事务状态信息，并用编程方式回滚，而不是通过异常的方式隐式回滚
 * <p>
 *     继承了SavepontManager，以提供对保存点管理器的访问。只有在基础事务管理器支持的情况下，
 *     保存管理点才可用
 *
 * @author Juergen Hoeller
 * @since 27.03.2003
 * @see #setRollbackOnly()
 * @see PlatformTransactionManager#getTransaction
 * @see org.springframework.transaction.support.TransactionCallback#doInTransaction
 * @see org.springframework.transaction.interceptor.TransactionInterceptor#currentTransactionStatus()
 */
public interface TransactionStatus extends SavepointManager, Flushable {

	/**
	 * Return whether the present transaction is new (else participating
	 * in an existing transaction, or potentially not running in an
	 * actual transaction in the first place).
	 * <p>判断当前事务是否是新事务（否则当前事务是一个已有事务，或者当前事务未运行在事务环境中）</p>
	 * @return
	 */
	boolean isNewTransaction();

	/**
	 * Return whether this transaction internally carries a savepoint,
	 * that is, has been created as nested transaction based on a savepoint.
	 * <p>This method is mainly here for diagnostic purposes, alongside
	 * {@link #isNewTransaction()}. For programmatic handling of custom
	 * savepoints, use SavepointManager's operations.
	 * <p>
	 *     返回事务是否有保存点，即已基于保存点创建了嵌套事务。
	 * <p>
	 *     此方法主要用于诊断目的，与{@link #isNewTransaction()}一起使用。
	 *     对于编程式处理保存点，请使用SavepointManager操作
	 * @return
	 * @see #isNewTransaction()
	 * @see #createSavepoint
	 * @see #rollbackToSavepoint(Object)
	 * @see #releaseSavepoint(Object)
	 */
	boolean hasSavepoint();

	/**
	 * Set the transaction rollback-only. This instructs the transaction manager
	 * that the only possible outcome of the transaction may be a rollback, as
	 * alternative to throwing an exception which would in turn trigger a rollback.
	 * <p>This is mainly intended for transactions managed by
	 * {@link org.springframework.transaction.support.TransactionTemplate} or
	 * {@link org.springframework.transaction.interceptor.TransactionInterceptor},
	 * where the actual commit/rollback decision is made by the container.
	 *
	 * <p>
	 *     设置事务可回滚。通过该标识通知事务管理器只能将事务回滚，事务管理通过显示调用回滚命令或者抛出异常的方式回滚事务
	 * <p>
	 *     主要用于{@link org.springframework.transaction.support.TransactionTemplate} 或者
	 *     {@link org.springframework.transaction.interceptor.TransactionInterceptor}管理的事务，
	 *     由容器决定是否提交/回滚
	 *
	 * @see org.springframework.transaction.support.TransactionCallback#doInTransaction
	 * @see org.springframework.transaction.interceptor.TransactionAttribute#rollbackOn
	 */
	void setRollbackOnly();

	/**
	 * Return whether the transaction has been marked as rollback-only
	 * (either by the application or by the transaction infrastructure).
	 * <p>
	 *     返回事务是否已标记为可回滚
	 * </p>
	 */
	boolean isRollbackOnly();

	/**
	 * Flush the underlying session to the datastore, if applicable:
	 * for example, all affected Hibernate/JPA sessions.
	 * <p>This is effectively just a hint and may be a no-op if the underlying
	 * transaction manager does not have a flush concept. A flush signal may
	 * get applied to the primary resource or to transaction synchronizations,
	 * depending on the underlying resource.
	 * <p>
	 *     如果可以，将基础会话刷新到数据存储区，例如Hibernate/JPA sessions
	 * <p>
	 *     实际上这只是提供了一个信号，如果事务管理器没有刷新功能，则可能不做任何事情。
	 *     刷新信号可能会应用于主要资源或者事务同步。
	 */
	@Override
	void flush();

	/**
	 * Return whether this transaction is completed, that is,
	 * whether it has already been committed or rolled back.
	 * <p>
	 *     返回此事务是否已完成，即是否已经提交或者回滚
	 * </p>
	 * @return
	 * @see PlatformTransactionManager#commit
	 * @see PlatformTransactionManager#rollback
	 */
	boolean isCompleted();

}
