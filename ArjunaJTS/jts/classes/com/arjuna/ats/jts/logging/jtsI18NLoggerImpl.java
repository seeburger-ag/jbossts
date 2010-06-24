/*
 * JBoss, Home of Professional Open Source
 * Copyright 2010, Red Hat, Inc. and/or its affiliates,
 * and individual contributors as indicated by the @author tags.
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
 * (C) 2010,
 * @author JBoss, by Red Hat.
 */
package com.arjuna.ats.jts.logging;

import com.arjuna.ats.arjuna.common.Uid;
import org.jboss.logging.Logger;

import java.text.MessageFormat;

import static org.jboss.logging.Logger.Level.*;

/**
 * i18n log messages for the jts module.
 * This class is autogenerated. Don't mess with it.
 *
 * @author Jonathan Halliday (jonathan.halliday@redhat.com) 2010-06
 */
public class jtsI18NLoggerImpl implements jtsI18NLogger {

    private final Logger logger;

    jtsI18NLoggerImpl(Logger logger) {
        this.logger = logger;
    }

    public void info_arjuna_recovery_ExpiredAssumedCompleteScanner_3(Uid arg0) {
        logger.logv(INFO, "ARJUNA-22003 Removing old assumed complete transaction {0}", arg0);
    }

    public void fatal_ORBManager() {
        logger.logv(FATAL, "ARJUNA-22006 The ORB has not been initialized yet", (Object)null);
    }

    public void warn_context_genfail(String arg0, Throwable arg1) {
        logger.logv(WARN, arg1, "ARJUNA-22008 {0} caught exception", arg0);
    }

    public void warn_context_orbnotsupported(String arg0, String arg1) {
        logger.logv(WARN, "ARJUNA-22009 {0} does not support ORB: {1}", arg0, arg1);
    }

    public String get_context_picreffail() {
        return "ARJUNA-22010 Failed when getting a reference to PICurrent.";
    }

    public void warn_cwabort(Throwable arg0) {
        logger.logv(WARN, arg0, "ARJUNA-22011 Failed to cancel transaction", (Object)null);
    }

    public void warn_cwcommit(Throwable arg0) {
        logger.logv(WARN, arg0, "ARJUNA-22012 Failed to mark transaction as rollback only", (Object)null);
    }

    public void warn_interposition_cwabort(Throwable arg0) {
        logger.logv(WARN, arg0, "ARJUNA-22013 Failed to cancel transaction", (Object)null);
    }

    public void warn_interposition_fldefault(String arg0) {
        logger.logv(WARN, "ARJUNA-22014 {0} - default already set!", arg0);
    }

    public void warn_interposition_resources_arjuna_ipfail(String arg0, Uid arg1) {
        logger.logv(WARN, "ARJUNA-22015 {0} - could not find {1} to remove.", arg0, arg1);
    }

    public void warn_interposition_resources_arjuna_ipnt() {
        logger.logv(WARN, "ARJUNA-22016 Nested transactions not identical.", (Object)null);
    }

    public void warn_interposition_resources_arjuna_ipnull() {
        logger.logv(WARN, "ARJUNA-22017 Interposed hierarchy is null!", (Object)null);
    }

    public void warn_interposition_resources_arjuna_iptl(Uid arg0, Uid arg1) {
        logger.logv(WARN, "ARJUNA-22018 TopLevel transactions not identical: {0} {1}", arg0, arg1);
    }

    public void warn_interposition_resources_arjuna_nochild(String arg0) {
        logger.logv(WARN, "ARJUNA-22019 {0} - error, no child found!", arg0);
    }

    public void warn_interposition_resources_arjuna_notchild(String arg0) {
        logger.logv(WARN, "ARJUNA-22020 {0} - not my child!", arg0);
    }

    public void warn_interposition_resources_arjuna_problemhierarchy(String arg0) {
        logger.logv(WARN, "ARJUNA-22021 hierarchy: {0}", arg0);
    }

    public void warn_interposition_sfcaught(String arg0, Uid arg1, Throwable arg2) {
        logger.logv(WARN, arg2, "ARJUNA-22022 {0} for transaction {1} caught exception", arg0, arg1);
    }

    public void warn_interposition_sfnoparent(String arg0) {
        logger.logv(WARN, "ARJUNA-22023 {0} - no parent transaction given!", arg0);
    }

    public void warn_orbspecific_coordinator_generror(String arg0, Throwable arg1) {
        logger.logv(WARN, arg1, "ARJUNA-22024 {0} caught exception", arg0);
    }

    public void warn_orbspecific_coordinator_rbofail(String arg0, Uid arg1, Throwable arg2) {
        logger.logv(WARN, arg2, "ARJUNA-22025 {0} attempt to mark transaction {1} as rollback only threw exception", arg0, arg1);
    }

    public void warn_orbspecific_coordinator_rccreate(Uid arg0, Throwable arg1) {
        logger.logv(WARN, arg1, "ARJUNA-22026 Creation of RecoveryCoordinator for {0} threw exception", arg0);
    }

    public String get_orbspecific_coordinator_rcnotcreated() {
        return "ARJUNA-22027 not created!";
    }

    public void warn_orbspecific_coordinator_txrun(String arg0) {
        logger.logv(WARN, "ARJUNA-22028 {0} called on still running transaction!", arg0);
    }

    public void warn_orbspecific_coordinator_uidfail(String arg0) {
        logger.logv(WARN, "ARJUNA-22029 {0} - could not get unique identifier of object.", arg0);
    }

    public void warn_orbspecific_coordinator_zsync(String arg0) {
        logger.logv(WARN, "ARJUNA-22030 {0} - none zero Synchronization list!", arg0);
    }

    public String get_orbspecific_destroyfailed() {
        return "ARJUNA-22031 could not destroy object:";
    }

    public void warn_orbspecific_interposition_coordinator_generror(String arg0, Throwable arg1) {
        logger.logv(WARN, arg1, "ARJUNA-22032 {0} caught exception", arg0);
    }

    public void warn_orbspecific_interposition_coordinator_syncerror(String arg0) {
        logger.logv(WARN, "ARJUNA-22033 {0} - synchronizations have not been called!", arg0);
    }

    public void warn_orbspecific_interposition_coordinator_txnotprepared(String arg0, String arg1) {
        logger.logv(WARN, "ARJUNA-22034 {0} - transaction not in prepared state: {1}", arg0, arg1);
    }

    public void warn_orbspecific_interposition_destfailed(String arg0, Throwable arg1) {
        logger.logv(WARN, arg1, "ARJUNA-22035 {0} could not destroy object", arg0);
    }

    public void warn_orbspecific_interposition_resources_arjuna_childerror(Uid arg0, Uid arg1) {
        logger.logv(WARN, "ARJUNA-22036 Could not remove child {0} from {1}", arg0, arg1);
    }

    public void warn_orbspecific_interposition_resources_arjuna_generror(String arg0, Throwable arg1) {
        logger.logv(WARN, arg1, "ARJUNA-22037 {0} caught exception", arg0);
    }

    public void warn_orbspecific_interposition_resources_arjuna_generror_2(String arg0, Throwable arg1) {
        logger.logv(WARN, arg1, "ARJUNA-22038 {0} caught exception", arg0);
    }

    public void warn_orbspecific_interposition_resources_arjuna_ipfailed(String arg0) {
        logger.logv(WARN, "ARJUNA-22039 {0} - could not register interposed hierarchy!", arg0);
    }

    public void warn_orbspecific_interposition_resources_arjuna_ipfailed_2(String arg0) {
        logger.logv(WARN, "ARJUNA-22040 {0} - could not register interposed hierarchy!", arg0);
    }

    public void warn_orbspecific_interposition_resources_arjuna_nocoord(String arg0) {
        logger.logv(WARN, "ARJUNA-22041 {0} - no coordinator to use!", arg0);
    }

    public void warn_orbspecific_interposition_resources_arjuna_notx(String arg0) {
        logger.logv(WARN, "ARJUNA-22042 {0} - no transaction!", arg0);
    }

    public void warn_orbspecific_interposition_resources_arjuna_nullcontrol_1(String arg0) {
        logger.logv(WARN, "ARJUNA-22043 {0} - attempt to commit with null control!", arg0);
    }

    public void warn_orbspecific_interposition_resources_arjuna_nullcontrol_2(String arg0) {
        logger.logv(WARN, "ARJUNA-22044 {0} - attempt to rollback transaction will null control!", arg0);
    }

    public void warn_orbspecific_interposition_resources_arjuna_nullcoord(String arg0) {
        logger.logv(WARN, "ARJUNA-22045 {0} - could not register as no Coordinator has been given!", arg0);
    }

    public void warn_orbspecific_interposition_resources_destroyfailed() {
        logger.logv(WARN, "ARJUNA-22046 Failed to destroy server-side synchronization object!", (Object)null);
    }

    public String get_orbspecific_interposition_resources_restricted_contx_1() {
        return "ARJUNA-22047 Concurrent children found for restricted interposition!";
    }

    public String get_orbspecific_interposition_resources_restricted_contx_4(String arg0) {
        return MessageFormat.format("ARJUNA-22048 {0} Concurrent children found for restricted interposition!", arg0);
    }

    public void warn_orbspecific_interposition_resources_restricted_contxfound_1(String arg0, String arg1) {
        logger.logv(WARN, "ARJUNA-22049 {0} - found concurrent ({1}) transactions!", arg0, arg1);
    }

    public void warn_orbspecific_interposition_resources_restricted_contxfound_3(String arg0, String arg1) {
        logger.logv(WARN, "ARJUNA-22050 {0} - found concurrent ({1}) transactions!", arg0, arg1);
    }

    public void warn_orbspecific_interposition_resources_stateerror(String arg0, String arg1, String arg2) {
        logger.logv(WARN, "ARJUNA-22051 {0} status of transaction is different from our status: <{1}, {2}>", arg0, arg1, arg2);
    }

    public void warn_orbspecific_interposition_resources_strict_ipfailed(String arg0) {
        logger.logv(WARN, "ARJUNA-22052 {0} - could not register interposed hierarchy!", arg0);
    }

    public void warn_orbspecific_interposition_resources_strict_iptlfailed(String arg0) {
        logger.logv(WARN, "ARJUNA-22053 {0} - could not register interposed hierarchy!", arg0);
    }

    public String get_orbspecific_invaliduid() {
        return "ARJUNA-22054 Invalid Uid:";
    }

    public String get_orbspecific_jacorb_interceptors_context_codeccreate() {
        return "ARJUNA-22055 Cannot create a codec of the required encoding.";
    }

    public void warn_orbspecific_jacorb_interceptors_context_codecerror(String arg0, String arg1, Throwable arg2) {
        logger.logv(WARN, arg2, "ARJUNA-22056 {0} - a failure occured when getting {1} codec - unknown encoding.", arg0, arg1);
    }

    public void warn_orbspecific_jacorb_interceptors_context_duplicatename(String arg0, String arg1, Throwable arg2) {
        logger.logv(WARN, arg2, "ARJUNA-22057 {0} - duplicate interceptor name for {1} when registering", arg0, arg1);
    }

    public void warn_orbspecific_jacorb_interceptors_context_error(Throwable arg0) {
        logger.logv(WARN, arg0, "ARJUNA-22058 Context interceptor caught an unexpected exception", (Object)null);
    }

    public String get_orbspecific_jacorb_interceptors_context_invalidparam() {
        return "ARJUNA-22059 Invalid portable interceptor transaction parameter!";
    }

    public String get_orbspecific_jacorb_interceptors_context_sie() {
        return "ARJUNA-22060 A server-side request interceptor already exists with that name.";
    }

    public void warn_orbspecific_jacorb_interceptors_context_srie(String arg0, Throwable arg1) {
        logger.logv(WARN, arg1, "ARJUNA-22061 {0} caught an unexpected exception", arg0);
    }

    public String get_orbspecific_jacorb_interceptors_interposition_codeccreate() {
        return "ARJUNA-22062 Cannot create a codec of the required encoding.";
    }

	public void warn_orbspecific_jacorb_interceptors_interposition_codecerror(String arg0, String arg1, Throwable arg2) {
		logger.logv(WARN, arg2, "ARJUNA-22063 {0} - a failure occured when getting {1} codec - unknown encoding.", arg0, arg1);
	}

    public void warn_orbspecific_jacorb_interceptors_interposition_duplicatename(String arg0, String arg1, Throwable arg2) {
        logger.logv(WARN, arg2, "ARJUNA-22064 {0} - duplicate interceptor name for {1} when registering", arg0, arg1);
    }

    public String get_orbspecific_jacorb_interceptors_interposition_invalidparam() {
        return "ARJUNA-22065 Invalid portable interceptor transaction parameter!";
    }

    public String get_orbspecific_jacorb_interceptors_interposition_sie() {
        return "ARJUNA-22066 A server-side request interceptor already exists with that name.";
    }

    public void warn_orbspecific_jacorb_interceptors_interposition_srie(String arg0, Throwable arg1) {
        logger.logv(WARN, arg1, "ARJUNA-22067 {0} caught an unexpected exception", arg0);
    }

    public void warn_orbspecific_jacorb_recoverycoordinators_ClientForwardInterceptor_2(Throwable arg0) {
        logger.logv(WARN, arg0, "ARJUNA-22069 Failed to retreive the Object reference of the default RecoverCoordinator Object.", (Object)null);
    }

    public void warn_orbspecific_jacorb_recoverycoordinators_ClientForwardInterceptor_4(Throwable arg0) {
        logger.logv(WARN, arg0, "ARJUNA-22071 Failed to build service context with the ObjectId", (Object)null);
    }

    public void warn_orbspecific_jacorb_recoverycoordinators_ClientInitializer_1(Throwable arg0) {
        logger.logv(WARN, arg0, "ARJUNA-22072 Failed in ClientInitializer::post_init -", (Object)null);
    }

    public void warn_orbspecific_jacorb_recoverycoordinators_JacOrbRCDefaultServant_3(Throwable arg0) {
        logger.logv(WARN, arg0, "ARJUNA-22075 JacOrbServant.replay_completion got exception", (Object)null);
    }

    public void warn_orbspecific_jacorb_recoverycoordinators_JacOrbRCManager_2(Throwable arg0) {
        logger.logv(WARN, arg0, "ARJUNA-22077 RCManager.makeRC did not make rcvco reference", (Object)null);
    }

    public void warn_orbspecific_jacorb_recoverycoordinators_JacOrbRCManager_3() {
        logger.logv(WARN, "ARJUNA-22078 RCManager could not find file in object store.", (Object)null);
    }

    public void warn_orbspecific_jacorb_recoverycoordinators_JacOrbRCManager_4() {
        logger.logv(WARN, "ARJUNA-22079 RCManager could not find file in object store during setup.", (Object)null);
    }

    public void warn_orbspecific_jacorb_recoverycoordinators_JacOrbRCManager_5(Throwable arg0) {
        logger.logv(WARN, arg0, "ARJUNA-22080 Unexpected exception during IOR setup", (Object)null);
    }

    public void warn_orbspecific_jacorb_recoverycoordinators_JacOrbRCServiceInit_1(Throwable arg0) {
        logger.logv(WARN, arg0, "ARJUNA-22081 Failed to create poa for recoverycoordinators", (Object)null);
    }

    public void warn_orbspecific_jacorb_recoverycoordinators_JacOrbRCServiceInit_3(Throwable arg0) {
        logger.logv(WARN, arg0, "ARJUNA-22083 JacOrbRCServiceInit - Failed to start RC service", (Object)null);
    }

    public void fatal_orbspecific_jacorb_recoverycoordinators_JacOrbRCServiceInit_5() {
        logger.logv(FATAL, "ARJUNA-22085 Unable to create file ObjectId - security problems", (Object)null);
    }

    public void info_orbspecific_jacorb_recoverycoordinators_JacOrbRCServiceInit_6(String arg0, String arg1) {
        logger.logv(INFO, "ARJUNA-22086 Starting RecoveryServer ORB on port {0} and address {1}", arg0, arg1);
    }

    public void warn_orbspecific_jacorb_recoverycoordinators_JacOrbRCServiceInit_6a(String arg0) {
        logger.logv(WARN, "ARJUNA-22087 Sharing RecoveryServer ORB on port {0}", arg0);
    }

    public void warn_orbspecific_jacorb_recoverycoordinators_JacOrbRCServiceInit_7(Throwable arg0) {
        logger.logv(WARN, arg0, "ARJUNA-22088 Failed to create orb and poa for transactional objects", (Object)null);
    }

    public void warn_orbspecific_jacorb_recoverycoordinators_JacOrbRCServiceInit_8() {
        logger.logv(WARN, "ARJUNA-22089 RootPOA is null. Initialization failed. Check no conflicting or duplicate service is running.", (Object)null);
    }

    public void warn_orbspecific_jacorb_recoverycoordinators_ServerInitializer_1(Throwable arg0) {
        logger.logv(WARN, arg0, "ARJUNA-22091 Failed in ServerInitializer::post_init -", (Object)null);
    }

    public String get_orbspecific_javaidl_interceptors_context_codeccreate() {
        return "ARJUNA-22093 Cannot create a codec of the required encoding.";
    }

    public void warn_orbspecific_javaidl_interceptors_context_codecerror(String arg0, String arg1, Throwable arg2) {
        logger.logv(WARN, arg2, "ARJUNA-22094 {0} - a failure occured when getting {1} codec - unknown encoding.", arg0, arg1);
    }

    public void warn_orbspecific_javaidl_interceptors_context_duplicatename(String arg0, String arg1, Throwable arg2) {
        logger.logv(WARN, arg2, "ARJUNA-22095 {0} - duplicate interceptor name for {1} when registering", arg0, arg1);
    }

    public void warn_orbspecific_javaidl_interceptors_context_error(Throwable arg0) {
        logger.logv(WARN, arg0, "ARJUNA-22096 Context interceptor caught an unexpected exception", (Object)null);
    }

    public String get_orbspecific_javaidl_interceptors_context_invalidparam() {
        return "ARJUNA-22097 Invalid portable interceptor transaction parameter!";
    }

    public String get_orbspecific_javaidl_interceptors_context_sie() {
        return "ARJUNA-22098 A server-side request interceptor already exists with that name.";
    }

    public void warn_orbspecific_javaidl_interceptors_context_srie(String arg0, Throwable arg1) {
        logger.logv(WARN, arg1, "ARJUNA-22099 {0} caught an unexpected exception", arg0);
    }

    public String get_orbspecific_javaidl_interceptors_interposition_codeccreate() {
        return "ARJUNA-22100 Cannot create a codec of the required encoding.";
    }

    public void warn_orbspecific_javaidl_interceptors_interposition_codecerror(String arg0, String arg1, Throwable arg2) {
        logger.logv(WARN, arg2, "ARJUNA-22101 {0} - a failure occured when getting {1} codec - unknown encoding.", arg0, arg1);
    }

    public void warn_orbspecific_javaidl_interceptors_interposition_duplicatename(String arg0, String arg1, Throwable arg2) {
        logger.logv(WARN, arg2, "ARJUNA-22102 {0} - duplicate interceptor name for {1} when registering", arg0, arg1);
    }

    public String get_orbspecific_javaidl_interceptors_interposition_invalidparam() {
        return "ARJUNA-22103 Invalid portable interceptor transaction parameter!";
    }

    public String get_orbspecific_javaidl_interceptors_interposition_sie() {
        return "ARJUNA-22104 A server-side request interceptor already exists with that name.";
    }

    public void warn_orbspecific_javaidl_interceptors_interposition_srie(String arg0, Throwable arg1) {
        logger.logv(WARN, arg1, "ARJUNA-22105 {0} caught an unexpected exception", arg0);
    }

    public String get_orbspecific_otiderror() {
        return "ARJUNA-22106 is not a valid unique identifier!";
    }

    public void warn_orbspecific_tficaught(String arg0, Uid arg1, Throwable arg2) {
        logger.logv(WARN, arg2, "ARJUNA-22107 {0} for {1} caught exception", arg0, arg1);
    }

    public void warn_orbspecific_tidyfail(String arg0, Throwable arg1) {
        logger.logv(WARN, arg1, "ARJUNA-22108 {0} attempt to clean up failed with exception", arg0);
    }

    public void warn_otsservererror(Throwable arg0) {
        logger.logv(WARN, arg0, "ARJUNA-22109 Resolution of OTS server failed", (Object)null);
    }

    public void warn_otsserverfailed(Throwable arg0) {
        logger.logv(WARN, arg0, "ARJUNA-22110 Resolution of OTS server failed - invalid name", (Object)null);
    }

    public void info_recovery_ExpiredContactScanner_3(Uid arg0) {
        logger.logv(INFO, "ARJUNA-22113 Removing old contact item {0}", arg0);
    }

    public void warn_recovery_RecoveryEnablement_1() {
        logger.logv(WARN, "ARJUNA-22116 Could not locate supported ORB for RecoveryCoordinator initialisation.", (Object)null);
    }

    public void warn_recovery_RecoveryEnablement_6(Throwable arg0) {
        logger.logv(WARN, arg0, "ARJUNA-22121 The Recovery Service Initialisation failed}", (Object)null);
    }

    public void fatal_recovery_RecoveryInit_4() {
        logger.logv(FATAL, "ARJUNA-22125 RecoveryCoordinator service can only be provided in RecoveryManager", (Object)null);
    }

    public void warn_recovery_contact_FactoryContactItem_1(Throwable arg0) {
        logger.logv(WARN, arg0, "ARJUNA-22127 Problem with storing process/factory link", (Object)null);
    }

    public void warn_recovery_contact_FactoryContactItem_2() {
        logger.logv(WARN, "ARJUNA-22128 Attempted to read FactoryContactItem of different version", (Object)null);
    }

    public void warn_recovery_contact_FactoryContactItem_3() {
        logger.logv(WARN, "ARJUNA-22129 Stored IOR is not an ArjunaFactory", (Object)null);
    }

    public void warn_recovery_contact_FactoryContactItem_4(Throwable arg0) {
        logger.logv(WARN, arg0, "ARJUNA-22130 Problem with restoring process/factory link", (Object)null);
    }

    public void warn_recovery_contact_FactoryContactItem_5() {
        logger.logv(WARN, "ARJUNA-22131 Problem with restoring process/factory link", (Object)null);
    }

    public void warn_recovery_contact_FactoryContactItem_6() {
        logger.logv(WARN, "ARJUNA-22132 Problem with storing process/factory link", (Object)null);
    }

    public void warn_recovery_contact_FactoryContactItem_7(Throwable arg0) {
        logger.logv(WARN, arg0, "ARJUNA-22133 Problem with removing contact item", (Object)null);
    }

    public void warn_recovery_contact_StatusChecker_10() {
        logger.logv(WARN, "ARJUNA-22138 NoTransaction exception on trying to contact original process", (Object)null);
    }

    public void warn_recovery_contact_StatusChecker_11(Throwable arg0) {
        logger.logv(WARN, arg0, "ARJUNA-22139 CORBA exception on trying to contact original process", (Object)null);
    }

    public void warn_recovery_contact_StatusChecker_12(Throwable arg0) {
        logger.logv(WARN, arg0, "ARJUNA-22140 Exception on trying to contact original process", (Object)null);
    }

    public void warn_recovery_contact_StatusChecker_14(Uid arg0) {
        logger.logv(WARN, "ARJUNA-22142 no known contactitem for {0}", arg0);
    }

    public void warn_recovery_contact_StatusChecker_15(Uid arg0, Throwable arg1) {
        logger.logv(WARN, arg1, "ARJUNA-22143 surprise item in StatusChecker list for {0}", arg0);
    }

    public void warn_recovery_contact_StatusChecker_3(Uid arg0) {
        logger.logv(WARN, "ARJUNA-22145 StatusChecked.getStatus - found intentions list for apparently unknown transaction: {0}", arg0);
    }

    public void warn_recovery_contact_StatusChecker_9() {
        logger.logv(WARN, "ARJUNA-22151 BAD_PARAM exception on trying to contact original process", (Object)null);
    }

    public void warn_recovery_rcnull(String arg0) {
        logger.logv(WARN, "ARJUNA-22152 {0} - being passed a null reference. Will ignore!", arg0);
    }

    public void warn_recovery_recoverycoordinators_GenericRecoveryCreator_1() {
        logger.logv(WARN, "ARJUNA-22156 GenericRecoveryCreator: Missing params to create", (Object)null);
    }

    public void warn_recovery_recoverycoordinators_RecoveryCoordinatorId_2(Throwable arg0) {
        logger.logv(WARN, arg0, "ARJUNA-22158 System exception when creating RecoveryCoordinator object key", (Object)null);
    }

    public void warn_recovery_recoverycoordinators_RecoveryCoordinatorId_3(String arg0) {
        logger.logv(WARN, "ARJUNA-22159 RecoveryCoordinatorId could not decode data {0}", arg0);
    }

    public void warn_recovery_recoveryinit_1() {
        logger.logv(WARN, "ARJUNA-22161 Failure recovery not supported for this ORB.", (Object)null);
    }

    public void warn_recovery_transactions_RecoveredServerTransaction_10(Uid arg0) {
        logger.logv(WARN, "ARJUNA-22167 Got TRANSIENT from ORB for tx {0} and assuming OBJECT_NOT_EXIST", arg0);
    }

    public void warn_recovery_transactions_RecoveredServerTransaction_12() {
        logger.logv(WARN, "ARJUNA-22169 RecoveredServerTransaction: caught NotPrepared", (Object)null);
    }

    public void warn_recovery_transactions_RecoveredServerTransaction_13(Throwable arg0) {
        logger.logv(WARN, arg0, "ARJUNA-22170 RecoveredServerTransaction: caught unexpected exception", (Object)null);
    }

    public void warn_recovery_transactions_RecoveredServerTransaction_14(Uid arg0) {
        logger.logv(WARN, "ARJUNA-22171 RecoveredServerTransaction: {0} is invalid", arg0);
    }

    public void warn_recovery_transactions_RecoveredServerTransaction_2(Uid arg0) {
        logger.logv(WARN, "ARJUNA-22175 RecoveredServerTransaction - activate of {0} failed!", arg0);
    }

    public void info_recovery_transactions_RecoveredServerTransaction_6(Uid arg0) {
        logger.logv(INFO, "ARJUNA-22178 ServerTransaction {0} unable determine status - retry later", arg0);
    }

    public void warn_recovery_transactions_RecoveredServerTransaction_7(String arg0) {
        logger.logv(WARN, "ARJUNA-22179 RecoveredServerTransaction.replayPhase2: unexpected Status: {0}", arg0);
    }

    public void warn_recovery_transactions_RecoveredTransaction_2(Uid arg0) {
        logger.logv(WARN, "ARJUNA-22183 RecoveredTransaction activate of {0} failed", arg0);
    }

    public void warn_recovery_transactions_RecoveredTransaction_3(Uid arg0, Throwable arg1) {
        logger.logv(WARN, arg1, "ARJUNA-22184 RecoveredTransaction activate of {0} failed", arg0);
    }

    public void warn_recovery_transactions_RecoveredTransaction_6(String arg0) {
        logger.logv(WARN, "ARJUNA-22186 RecoveredTransaction.replayPhase2 for {0} failed", arg0);
    }

    public void warn_recovery_transactions_RecoveredTransaction_8(Throwable arg0) {
        logger.logv(WARN, arg0, "ARJUNA-22188 RecoveredTransaction.removeOldStoreEntry - problem", (Object)null);
    }

    public void info_recovery_transactions_ServerTransactionRecoveryModule_3() {
        logger.logv(INFO, "ARJUNA-22190 ServerTransactionRecoveryModule - First Pass", (Object)null);
    }

    public void info_recovery_transactions_ServerTransactionRecoveryModule_4() {
        logger.logv(INFO, "ARJUNA-22191 ServerTransactionRecoveryModule - Second Pass", (Object)null);
    }

    public void info_recovery_transactions_ServerTransactionRecoveryModule_5(Uid arg0) {
        logger.logv(INFO, "ARJUNA-22192 ServerTransactionRecoveryModule - Transaction {0} still in ActionStore", arg0);
    }

    public void info_recovery_transactions_TopLevelTransactionRecoveryModule_3() {
        logger.logv(INFO, "ARJUNA-22199 TopLevelTransactionRecoveryModule First Pass", (Object)null);
    }

    public void info_recovery_transactions_TopLevelTransactionRecoveryModule_4() {
        logger.logv(INFO, "ARJUNA-22200 TopLevelTransactionRecoveryModule Second Pass", (Object)null);
    }

    public void warn_recovery_transactions_TransactionCacheItem_2(String arg0) {
        logger.logv(WARN, "ARJUNA-22202 TransactionCacheItem.loadTransaction - unknown type: {0}", arg0);
    }

    public void info_recovery_transactions_TransactionCache_4(Uid arg0) {
        logger.logv(INFO, "ARJUNA-22206 Transaction {0} assumed complete - will not poll any more", arg0);
    }

    public void info_recovery_transactions_TransactionCache_5(Uid arg0) {
        logger.logv(INFO, "ARJUNA-22207 Transaction {0} recovery completed", arg0);
    }

    public void info_recovery_transactions_TransactionRecoveryModule_11() {
        logger.logv(INFO, "ARJUNA-22213 TransactionRecoveryModule.periodicWorkFirstPass()", (Object)null);
    }

    public void info_recovery_transactions_TransactionRecoveryModule_12() {
        logger.logv(INFO, "ARJUNA-22214 TransactionRecoveryModule.periodicWorkSecondPass()", (Object)null);
    }

    public void warn_recovery_transactions_TransactionRecoveryModule_2() {
        logger.logv(WARN, "ARJUNA-22215 TransactionRecoveryModule: transaction type not set", (Object)null);
    }

    public void warn_recovery_transactions_TransactionRecoveryModule_4(Throwable arg0) {
        logger.logv(WARN, arg0, "ARJUNA-22217 TransactionRecoveryModule: Object store exception", (Object)null);
    }

    public void info_recovery_transactions_TransactionRecoveryModule_6(Uid arg0) {
        logger.logv(INFO, "ARJUNA-22219 Transaction {0} still in ActionStore", arg0);
    }

    public void warn_resources_errgenerr(String arg0, Throwable arg1) {
        logger.logv(WARN, arg1, "ARJUNA-22223 {0} caught exception", arg0);
    }

    public void warn_resources_errnoparent(String arg0) {
        logger.logv(WARN, "ARJUNA-22224 {0} - no parent!", arg0);
    }

    public void warn_resources_errnores(String arg0) {
        logger.logv(WARN, "ARJUNA-22225 {0} called without a resource reference!", arg0);
    }

    public void warn_resources_errsavefail(String arg0, String arg1) {
        logger.logv(WARN, "ARJUNA-22226 {0} failed. Returning default value: {1}", arg0, arg1);
    }

    public void warn_resources_errsetvalue(String arg0) {
        logger.logv(WARN, "ARJUNA-22227 {0} called illegally!", arg0);
    }

    public void warn_resources_errtypefail(String arg0, String arg1) {
        logger.logv(WARN, "ARJUNA-22228 {0} failed. Returning default value: {1}", arg0, arg1);
    }

    public void warn_resources_noparent(Uid arg0) {
        logger.logv(WARN, "ARJUNA-22229 {0} has no parent transaction!", arg0);
    }

    public void warn_resources_rrcaught(String arg0, Throwable arg1) {
        logger.logv(WARN, arg1, "ARJUNA-22230 {0} caught exception", arg0);
    }

    public void warn_resources_rrillegalvalue(String arg0) {
        logger.logv(WARN, "ARJUNA-22231 {0} called illegally.", arg0);
    }

    public void warn_resources_rrinvalid(String arg0) {
        logger.logv(WARN, "ARJUNA-22232 {0} called without a resource!", arg0);
    }

    public void warn_eicaughtexception(String arg0, Throwable arg1) {
        logger.logv(WARN, arg1, "ARJUNA-22233 {0} caught unexpected exception", arg0);
    }

    public void warn_excalledagain(String arg0) {
        logger.logv(WARN, "ARJUNA-22234 {0} called multiple times.", arg0);
    }

    public void warn_extensions_abortfail(String arg0) {
        logger.logv(WARN, "ARJUNA-22235 Could not rollback transaction {0}", arg0);
    }

    public void warn_extensions_abortfailnoexist(String arg0) {
        logger.logv(WARN, "ARJUNA-22236 Could not rollback transaction {0} as it does not exist!", arg0);
    }

    public void warn_extensions_atcannotabort(String arg0, String arg1) {
        logger.logv(WARN, "ARJUNA-22237 {0} - cannot rollback {1}", arg0, arg1);
    }

    public void warn_extensions_atgenerror(String arg0, Throwable arg1) {
        logger.logv(WARN, arg1, "ARJUNA-22239 {0} caught unexpected exception", arg0);
    }

    public void warn_extensions_atnovalidtx(String arg0) {
        logger.logv(WARN, "ARJUNA-22240 {0} - no transaction!", arg0);
    }

    public void warn_extensions_atoutofseq(String arg0, String arg1) {
        logger.logv(WARN, "ARJUNA-22241 {0} - terminated out of sequence {1}", arg0, arg1);
    }

    public void warn_extensions_atscope(String arg0, Uid arg1) {
        logger.logv(WARN, "ARJUNA-22242 {0} - running atomic transaction going out of scope. Will roll back. {1}", arg0, arg1);
    }

    public void warn_extensions_atunavailable(String arg0) {
        logger.logv(WARN, "ARJUNA-22243 {0} - transaction unavailable.", arg0);
    }

    public void warn_extensions_atwillabort(String arg0) {
        logger.logv(WARN, "ARJUNA-22244 Will roll back. Current transaction is {0}", arg0);
    }

    public void warn_extensions_namefail(Throwable arg0) {
        logger.logv(WARN, arg0, "ARJUNA-22245 Cannot determine transaction name!", (Object)null);
    }

    public void warn_extensions_threadasserror(String arg0, Throwable arg1) {
        logger.logv(WARN, arg1, "ARJUNA-22246 {0} caught exception", arg0);
    }

    public void warn_extensions_tltnestedscope(String arg0) {
        logger.logv(WARN, "ARJUNA-22247 Top-level transaction going out of scope with nested transaction {0} still set.", arg0);
    }

    public void warn_exunregfail(String arg0) {
        logger.logv(WARN, "ARJUNA-22248 {0} - could not unregister from transaction!", arg0);
    }

    public void warn_thread_resumefailed(String arg0, Throwable arg1) {
        logger.logv(WARN, arg1, "ARJUNA-22249 {0} - could not resume transaction", arg0);
    }

    public String get_thread_resumefailederror() {
        return "ARJUNA-22250 could not resume transaction:";
    }

    public void warn_utils_ORBSetup_orbalreadyset() {
        logger.logv(WARN, "ARJUNA-22251 The ORBManager is already associated with an ORB/OA.", (Object)null);
    }

    public void warn_recoveredServerTransaction_removeOldStoreEntry(Throwable arg0) {
		logger.logv(WARN, arg0, "ARJUNA-22252 Failed to remove old ObjectStore entry", (Object)null);
	}

	public String get_orbspecific_jacorb_interceptors_context_cie() {
		return "ARJUNA-22253 A client-side request interceptor already exists with that name.";
	}

	public String get_orbspecific_jacorb_interceptors_interposition_cie() {
		return "ARJUNA-22254 A client-side request interceptor already exists with that name.";
	}

	public String get_orbspecific_javaidl_interceptors_context_cie() {
		return "ARJUNA-22255 A client-side request interceptor already exists with that name.";
	}

	public String get_orbspecific_javaidl_interceptors_interposition_cie() {
		return "ARJUNA-22256 A client-side request interceptor already exists with that name.";
	}

	public void warn_orbspecific_coordinator_ipunknown(String arg0, String arg1) {
		logger.logv(WARN, "ARJUNA-22257 {0} - unknown interposition type: {1}", arg0, arg1);
	}
}
