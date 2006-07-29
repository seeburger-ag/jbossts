/*
 * JBoss, Home of Professional Open Source
 * Copyright 2006, JBoss Inc., and others contributors as indicated 
 * by the @authors tag. All rights reserved. 
 * See the copyright.txt in the distribution for a
 * full listing of individual contributors. 
 * This copyrighted material is made available to anyone wishing to use,
 * modify, copy, or redistribute it subject to the terms and conditions
 * of the GNU Lesser General Public License, v. 2.1.
 * This program is distributed in the hope that it will be useful, but WITHOUT A 
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A 
 * PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 * You should have received a copy of the GNU Lesser General Public License,
 * v.2.1 along with this distribution; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, 
 * MA  02110-1301, USA.
 * 
 * (C) 2005-2006,
 * @author JBoss Inc.
 */
/*
 * Copyright (C) 1998, 1999, 2000, 2001,
 *
 * Arjuna Solutions Limited,
 * Newcastle upon Tyne,
 * Tyne and Wear,
 * UK.
 *
 * $Id: TransactionReaper.java 2342 2006-03-30 13:06:17Z  $
 */

package com.arjuna.ats.arjuna.coordinator;

import com.arjuna.ats.arjuna.common.Environment;
import com.arjuna.ats.arjuna.common.arjPropertyManager;
import com.arjuna.ats.arjuna.coordinator.Reapable;
import com.arjuna.ats.arjuna.coordinator.ActionStatus;

import com.arjuna.ats.internal.arjuna.template.OrderedList;
import com.arjuna.ats.internal.arjuna.template.OrderedListIterator;
import com.arjuna.ats.internal.arjuna.coordinator.*;

import com.arjuna.ats.arjuna.logging.tsLogger;
import com.arjuna.ats.arjuna.logging.FacilityCode;

import com.arjuna.common.util.logging.*;

/**
 * Class to record transactions with non-zero timeout values, and class to
 * implement a transaction reaper thread which terminates these transactions
 * once their timeout elapses.
 * 
 * @author Mark Little (mark@arjuna.com)
 * @version $Id: TransactionReaper.java 2342 2006-03-30 13:06:17Z  $
 * @since JTS 1.0.
 * 
 * 
 * @message com.arjuna.ats.arjuna.coordinator.TransactionReaper_1
 *          [com.arjuna.ats.arjuna.coordinator.TransactionReaper_1] -
 *          TransactionReaper - could not create transaction list. Out of
 *          memory.
 * @message com.arjuna.ats.arjuna.coordinator.TransactionReaper_2
 *          [com.arjuna.ats.arjuna.coordinator.TransactionReaper_2] -
 *          TransactionReaper::check - comparing {0}
 * @message com.arjuna.ats.arjuna.coordinator.TransactionReaper_3
 *          [com.arjuna.ats.arjuna.coordinator.TransactionReaper_3] -
 *          TransactionReaper::check - rollback for {0}
 * @message com.arjuna.ats.arjuna.coordinator.TransactionReaper_4
 *          [com.arjuna.ats.arjuna.coordinator.TransactionReaper_4] -
 *          TransactionReaper failed to force rollback on {0}
 * @message com.arjuna.ats.arjuna.coordinator.TransactionReaper_5
 *          [com.arjuna.ats.arjuna.coordinator.TransactionReaper_5] -
 *          TransactionReaper failed to force rollback_only on {0}
 * @message com.arjuna.ats.arjuna.coordinator.TransactionReaper_6
 *          [com.arjuna.ats.arjuna.coordinator.TransactionReaper_6] -
 *          TransactionReaper::getTimeout for {0} returning {1}
 */

public class TransactionReaper
{

	public static final String NORMAL = "NORMAL";
	public static final String DYNAMIC = "DYNAMIC";

	public TransactionReaper (long checkPeriod)
	{
		if (tsLogger.arjLogger.debugAllowed())
		{
			tsLogger.arjLogger.debug(DebugLevel.CONSTRUCTORS, VisibilityLevel.VIS_PUBLIC, FacilityCode.FAC_ATOMIC_ACTION, "TransactionReaper::TransactionReaper ( "
					+ checkPeriod + " )");
		}

		_checkPeriod = checkPeriod;

		if (_list == null)
		{
			if (tsLogger.arjLoggerI18N.isFatalEnabled())
			{
				tsLogger.arjLoggerI18N.fatal("com.arjuna.ats.arjuna.coordinator.TransactionReaper_1");
			}

			throw new OutOfMemoryError();
		}
	}

	public void finalize ()
	{
		if (tsLogger.arjLogger.debugAllowed())
		{
			tsLogger.arjLogger.debug(DebugLevel.DESTRUCTORS, VisibilityLevel.VIS_PUBLIC, FacilityCode.FAC_ATOMIC_ACTION, "TransactionReaper.finalize ()");
		}

		_list = null;
	}

	public final synchronized long checkingPeriod ()
	{
		if (_dynamic && (_list.size() > 0))
		{
			final ReaperElement head = (ReaperElement)_list.peak() ;
			return head._absoluteTimeout - System.currentTimeMillis() ;
		}
		return _checkPeriod;
	}

	/*
	 * Should be no need to protect with a mutex since only one thread is ever
	 * doing the work.
	 */

	/**
	 * Only check for one at a time to prevent starvation.
	 * 
	 * Timeout is given in milliseconds.
	 */

	public final boolean check()
	{
		if (tsLogger.arjLogger.debugAllowed())
		{
			tsLogger.arjLogger.debug(DebugLevel.FUNCTIONS, VisibilityLevel.VIS_PUBLIC, FacilityCode.FAC_ATOMIC_ACTION, "TransactionReaper::check ()");
		}

		if (_list.size() == 0)
			return true;

		OrderedListIterator iter = new OrderedListIterator(_list);
		ReaperElement e = null;

		while ((e = (ReaperElement) iter.iterate()) != null)
		{
			if (tsLogger.arjLoggerI18N.debugAllowed())
			{
				tsLogger.arjLoggerI18N.debug(DebugLevel.FUNCTIONS, VisibilityLevel.VIS_PUBLIC, FacilityCode.FAC_ATOMIC_ACTION, "com.arjuna.ats.arjuna.coordinator.TransactionReaper_2", new Object[]
				{ Long.toString(e._absoluteTimeout) });
			}

			final long now = System.currentTimeMillis() ;
			System.err.println("TS_DEBUG: Reaper comparing current time " + now + " with transaction lifetime of " + e._absoluteTimeout) ;

			if (now >= e._absoluteTimeout)
			{
				if (e._control.running())
				{
					/*
				 	* If this is a local transaction, then we can roll it back
				 	* completely. Otherwise, just mark it as rollback only.
				 	*/

					boolean problem = false;

					try
					{
						if (e._control.cancel() == ActionStatus.ABORTED)
						{
							if (tsLogger.arjLoggerI18N.debugAllowed())
							{
								tsLogger.arjLoggerI18N.debug(DebugLevel.FUNCTIONS, VisibilityLevel.VIS_PUBLIC, FacilityCode.FAC_ATOMIC_ACTION, "com.arjuna.ats.arjuna.coordinator.TransactionReaper_3", new Object[]
								{ e._control.get_uid() });
							}
						}
						else
							problem = true;
					}
					catch (Exception ex2)
					{
						if (tsLogger.arjLoggerI18N.isWarnEnabled())
						{
							tsLogger.arjLoggerI18N.warn("com.arjuna.ats.arjuna.coordinator.TransactionReaper_4", new Object[]
							{ e._control });
						}

						problem = true;
					}

					if (problem)
					{
						boolean error = false;
						boolean printDebug = tsLogger.arjLoggerI18N.isWarnEnabled();

						try
						{
							error = !e._control.preventCommit();
						}
						catch (Exception ex3)
						{
							error = true;
						}

						if (error || printDebug)
						{
							if (error)
							{
								if (tsLogger.arjLoggerI18N.isWarnEnabled())
								{
									tsLogger.arjLoggerI18N.warn("com.arjuna.ats.arjuna.coordinator.TransactionReaper_5", new Object[]
									{ e._control });
								}
							}
							else
							{
								if (tsLogger.arjLoggerI18N.debugAllowed())
								{
									tsLogger.arjLoggerI18N.debug(DebugLevel.FUNCTIONS, VisibilityLevel.VIS_PUBLIC, FacilityCode.FAC_ATOMIC_ACTION, "com.arjuna.ats.arjuna.coordinator.TransactionReaper_3", new Object[]
									{ e._control });
								}
							}
						}
					}
				}

				_list.remove(e);
			}
			else
			{
				break;
			}
		}

		return true;
	}

	/**
	 * @return the number of items in the reaper's list.
	 * @since JTS 2.2.
	 */

	public final long numberOfTransactions ()
	{
		return _list.size();
	}

	/**
	 * timeout is given in seconds, but we work in milliseconds.
	 */

	public final boolean insert (Reapable control, int timeout)
	{
		if (tsLogger.arjLogger.debugAllowed())
		{
			tsLogger.arjLogger.debug(DebugLevel.FUNCTIONS, VisibilityLevel.VIS_PUBLIC, FacilityCode.FAC_ATOMIC_ACTION, "TransactionReaper::insert ( "
					+ control + ", " + timeout + " )");
		}

		/*
		 * Ignore if the timeout is zero, since this means the transaction
		 * should never timeout.
		 */

		if (timeout == 0)
			return true;

		ReaperElement e = new ReaperElement(control, timeout);

		synchronized (this)
		{
			TransactionReaper._lifetime += timeout;

			/*
		 	* If the timeout for this transaction is less than the current timeout
		 	* for the reaper thread (or one is not set for the reaper thread) then
		 	* use that timeout and interrupt the thread to get it to recheck.
		 	*/

			final long timeoutms = timeout*1000 ;
			if ((timeoutms < _checkPeriod) || (_checkPeriod == Long.MAX_VALUE))
			{
				_checkPeriod = timeoutms ; // convert to milliseconds!
				TransactionReaper._reaperThread.interrupt();
			}

			if (_list.insert(e))
				return true;
			else
			{
				e = null;
				return false;
			}
		}
	}

	public final boolean remove (java.lang.Object control)
	{
		if (tsLogger.arjLogger.debugAllowed())
		{
			tsLogger.arjLogger.debug(DebugLevel.FUNCTIONS, VisibilityLevel.VIS_PUBLIC, FacilityCode.FAC_ATOMIC_ACTION, "TransactionReaper::remove ( "
					+ control + " )");
		}

		if (control == null)
			return false;

		boolean result = false;

		synchronized (this)
		{
			ReaperElement e = null;
			OrderedListIterator iter = new OrderedListIterator(_list);
			boolean found = false;

			while (!found && ((e = (ReaperElement) iter.iterate()) != null))
			{
				try
				{
					found = e._control.equals(control);

					break;
				}
				catch (Exception e2)
				{
					break;
				}
			}

			iter = null;

			if (found)
			{
				result = _list.remove(e);

				e = null;
			}
		}

		return result;
	}

	/**
	 * Given a Control, return the associated timeout, or 0 if we do not know
	 * about it.
	 * 
	 * Return in seconds!
	 */

	public final int getTimeout (Object control)
	{
		if ((_list.size() == 0) || (control == null))
		{
			if (tsLogger.arjLogger.debugAllowed())
			{
				tsLogger.arjLogger.debug(DebugLevel.FUNCTIONS, VisibilityLevel.VIS_PUBLIC, FacilityCode.FAC_ATOMIC_ACTION, "TransactionReaper::getTimeout for "
						+ control + " returning 0");
			}

			return 0;
		}

		ReaperElement e = null;
		OrderedListIterator iter = new OrderedListIterator(_list);

		while ((e = (ReaperElement) iter.iterate()) != null)
		{
			try
			{
				if (e._control.equals(control))
				{
					iter = null;

					if (tsLogger.arjLoggerI18N.debugAllowed())
					{
						tsLogger.arjLoggerI18N.debug(DebugLevel.FUNCTIONS, VisibilityLevel.VIS_PUBLIC, FacilityCode.FAC_ATOMIC_ACTION, "com.arjuna.ats.arjuna.coordinator.TransactionReaper_6", new Object[]
						{ control, Integer.toString(e._timeout) });
					}

					return e._timeout;
				}
			}
			catch (Exception e2)
			{
				break;
			}
		}

		if (tsLogger.arjLoggerI18N.debugAllowed())
		{
			tsLogger.arjLoggerI18N.debug(DebugLevel.FUNCTIONS, VisibilityLevel.VIS_PUBLIC, FacilityCode.FAC_ATOMIC_ACTION, "com.arjuna.ats.arjuna.coordinator.TransactionReaper_6", new Object[]
			{ control, "0" });
		}

		return 0;
	}

	/**
	 * Currently we let the reaper thread run at same priority as other threads.
	 * Could get priority from environment.
	 */

	public static synchronized TransactionReaper create (long checkPeriod)
	{
		if (tsLogger.arjLogger.debugAllowed())
		{
			tsLogger.arjLogger.debug(DebugLevel.FUNCTIONS, VisibilityLevel.VIS_PUBLIC, FacilityCode.FAC_ATOMIC_ACTION, "TransactionReaper::create ( "
					+ checkPeriod + " )");
		}

		if (TransactionReaper._theReaper == null)
		{
			String mode = arjPropertyManager.propertyManager.getProperty(Environment.TX_REAPER_MODE);

			if (mode != null)
			{
				if (mode.compareTo(TransactionReaper.DYNAMIC) == 0)
					TransactionReaper._dynamic = true;
			}

			if (!TransactionReaper._dynamic)
			{
				String timeoutEnv = arjPropertyManager.propertyManager.getProperty(Environment.TX_REAPER_TIMEOUT);

				if (timeoutEnv != null)
				{
					Long l = null;

					try
					{
						l = new Long(timeoutEnv);
						checkPeriod = l.longValue();

						l = null;
					}
					catch (NumberFormatException e)
					{
						tsLogger.arjLogger.warn("TransactionReaper::create - "
								+ e);
					}
				}
			}
			else
				checkPeriod = Long.MAX_VALUE;

			TransactionReaper._theReaper = new TransactionReaper(checkPeriod);

			_reaperThread = new ReaperThread(TransactionReaper._theReaper);
			// _reaperThread.setPriority(Thread.MIN_PRIORITY);

			_reaperThread.setDaemon(true);

			_reaperThread.start();
		}

		return TransactionReaper._theReaper;
	}

	public static TransactionReaper create ()
	{
		return create(TransactionReaper.defaultCheckPeriod);
	}

	public static TransactionReaper transactionReaper ()
	{
		return transactionReaper(false);
	}

	/*
	 * If parameter is true then do a create.
	 */

	public static synchronized TransactionReaper transactionReaper (boolean createReaper)
	{
		if (createReaper)
			return create();
		else
			return _theReaper;
	}

	/*
	 * Don't bother synchronizing as this is only an estimate anyway.
	 */

	public static final synchronized long transactionLifetime ()
	{
		return TransactionReaper._lifetime;
	}

	public static final long defaultCheckPeriod = 120000; // in milliseconds

	static final void reset ()
	{
		_theReaper = null;
	}

	private OrderedList _list = new OrderedList();
	private long _checkPeriod = 0;
	
	private static TransactionReaper _theReaper = null;
	private static ReaperThread _reaperThread = null;
	private static boolean _dynamic = false;
	private static long _lifetime = 0;

}
