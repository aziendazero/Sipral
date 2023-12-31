
--
-- \jboss-as\server\production\deploy\jboss-messaging.sar\clustered-mysql-persistence-service.xml
--    <mbean code="org.jboss.messaging.core.jmx.JDBCPersistenceManagerService"
--      name="jboss.messaging:service=PersistenceManager"
--      xmbean-dd="xmdesc/JDBCPersistenceManager-xmbean.xml">


  DROP TABLE "HILOSEQUENCES" cascade constraints;
  DROP TABLE "JBM_COUNTER" cascade constraints;
  DROP TABLE "JBM_DUAL" cascade constraints;
  DROP TABLE "JBM_ID_CACHE" cascade constraints;
  DROP TABLE "JBM_MSG" cascade constraints;
  DROP TABLE "JBM_MSG_REF" cascade constraints;
  DROP TABLE "JBM_POSTOFFICE" cascade constraints;
  DROP TABLE "JBM_ROLE" cascade constraints;
  DROP TABLE "JBM_TX" cascade constraints;
  DROP TABLE "JBM_USER" cascade constraints;
  DROP TABLE "TIMERS" cascade constraints;


CREATE TABLE JBM_DUAL (DUMMY INTEGER, PRIMARY KEY (DUMMY));

CREATE TABLE JBM_MSG_REF (MESSAGE_ID INTEGER, CHANNEL_ID INTEGER, TRANSACTION_ID INTEGER, STATE CHAR(1), ORD INTEGER, PAGE_ORD INTEGER, DELIVERY_COUNT INTEGER, SCHED_DELIVERY INTEGER, PRIMARY KEY(MESSAGE_ID, CHANNEL_ID));
CREATE INDEX JBM_MSG_REF_TX ON JBM_MSG_REF (TRANSACTION_ID, STATE);

CREATE TABLE JBM_MSG (MESSAGE_ID INTEGER, RELIABLE CHAR(1), EXPIRATION INTEGER, TIMESTAMP INTEGER, PRIORITY INTEGER, TYPE INTEGER, HEADERS BLOB, PAYLOAD BLOB, PRIMARY KEY (MESSAGE_ID));

CREATE TABLE JBM_TX (NODE_ID INTEGER, TRANSACTION_ID INTEGER, BRANCH_QUAL RAW(254), FORMAT_ID INTEGER, GLOBAL_TXID RAW(254), PRIMARY KEY (TRANSACTION_ID));

CREATE TABLE JBM_COUNTER (NAME VARCHAR2(255), NEXT_ID INTEGER, PRIMARY KEY(NAME));

CREATE TABLE JBM_ID_CACHE (NODE_ID INTEGER, CNTR INTEGER, JBM_ID VARCHAR2(255), PRIMARY KEY(NODE_ID, CNTR));

--   <mbean code="org.jboss.messaging.core.jmx.MessagingPostOfficeService"
--    name="jboss.messaging:service=PostOffice"
--    xmbean-dd="xmdesc/MessagingPostOffice-xmbean.xml">
CREATE TABLE JBM_POSTOFFICE (POSTOFFICE_NAME VARCHAR2(255), NODE_ID INTEGER, QUEUE_NAME VARCHAR2(255), COND VARCHAR2(1023), SELECTOR VARCHAR2(1023), CHANNEL_ID INTEGER, CLUSTERED CHAR(1), ALL_NODES CHAR(1), PRIMARY KEY(POSTOFFICE_NAME, NODE_ID, QUEUE_NAME));

--    <mbean code="org.jboss.messaging.core.jmx.MessagingPostOfficeService"
--      name="jboss.messaging:service=PostOffice"
--      xmbean-dd="xmdesc/MessagingPostOffice-xmbean.xml">
CREATE TABLE JBM_USER (USER_ID VARCHAR2(32) NOT NULL, PASSWD VARCHAR2(32) NOT NULL, CLIENTID VARCHAR2(128), PRIMARY KEY(USER_ID));

CREATE TABLE JBM_ROLE (ROLE_ID VARCHAR2(32) NOT NULL, USER_ID VARCHAR2(32) NOT NULL, PRIMARY KEY(USER_ID, ROLE_ID));

INSERT INTO JBM_USER (USER_ID, PASSWD) VALUES ('guest', 'guest');
INSERT INTO JBM_USER (USER_ID, PASSWD) VALUES ('j2ee', 'j2ee');
INSERT INTO JBM_USER (USER_ID, PASSWD, CLIENTID) VALUES ('john', 'needle', 'DurableSubscriberExample');
INSERT INTO JBM_USER (USER_ID, PASSWD) VALUES ('nobody', 'nobody');
INSERT INTO JBM_USER (USER_ID, PASSWD) VALUES ('dynsub', 'dynsub');
INSERT INTO JBM_USER (USER_ID, PASSWD) VALUES ('esbuser', 'esbpassword');
INSERT INTO JBM_USER (USER_ID, PASSWD) VALUES ('phijms', 'jms4phi');

INSERT INTO JBM_ROLE (ROLE_ID, USER_ID) VALUES ('guest','guest');
INSERT INTO JBM_ROLE (ROLE_ID, USER_ID) VALUES ('j2ee','guest');
INSERT INTO JBM_ROLE (ROLE_ID, USER_ID) VALUES ('john','guest');
INSERT INTO JBM_ROLE (ROLE_ID, USER_ID) VALUES ('subscriber','john');
INSERT INTO JBM_ROLE (ROLE_ID, USER_ID) VALUES ('publisher','john');
INSERT INTO JBM_ROLE (ROLE_ID, USER_ID) VALUES ('publisher','dynsub');
INSERT INTO JBM_ROLE (ROLE_ID, USER_ID) VALUES ('durpublisher','john');
INSERT INTO JBM_ROLE (ROLE_ID, USER_ID) VALUES ('durpublisher','dynsub');
INSERT INTO JBM_ROLE (ROLE_ID, USER_ID) VALUES ('noacc','nobody');
INSERT INTO JBM_ROLE (ROLE_ID, USER_ID) VALUES ('phi','phijms');

--
-- \jboss-as\server\production\deploy\uuid-key-generator.sar\META-INF\jboss-service.xml
--
CREATE TABLE HILOSEQUENCES (SEQUENCENAME VARCHAR2(50 CHAR) NOT NULL, HIGHVALUES NUMBER(10,0) NOT NULL, PRIMARY KEY(SEQUENCENAME));


CREATE TABLE timers (
  TIMERID VARCHAR2(80 CHAR) NOT NULL,
  TARGETID VARCHAR2(250 CHAR) NOT NULL,
  INITIALDATE DATE DEFAULT SYSDATE NOT NULL,
  TIMERINTERVAL NUMBER(19,0),
  INSTANCEPK BLOB,
  INFO BLOB,
  PRIMARY KEY(TIMERID, TARGETID)
);