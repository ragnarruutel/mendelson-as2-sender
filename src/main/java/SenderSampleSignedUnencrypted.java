import de.mendelson.comm.as2.integration.AS2IntegrationConstants;
import de.mendelson.comm.as2.integration.AS2MDNParser;
import de.mendelson.comm.as2.integration.AS2MessageSender;
import de.mendelson.comm.as2.integration.KeystoreInformation;
import de.mendelson.comm.as2.integration.data.AS2RemoteHTTPData;
import de.mendelson.util.security.BCCryptoHelper;

import java.util.logging.Level;
import java.util.logging.Logger;

public class SenderSampleSignedUnencrypted {

    public SenderSampleSignedUnencrypted() {
        new BCCryptoHelper().initialize();
    }

    public static final void main(String[] args) {

        AS2MessageSender sender = new AS2MessageSender();
        try {
            Logger logger = Logger.getAnonymousLogger();
            sender.setLogger(logger);
            sender.setAS2IdSender("xxx");
            sender.setAS2IdReceiver("yyy");
            sender.setCompression(AS2IntegrationConstants.COMPRESSION_NONE);
            sender.setURL("http://localhost:8080/as2/HttpReceiver");
            sender.setRequestSignedMDN(false);
            sender.setPayload("Hello world".getBytes(), "mytestfile.txt");
            KeystoreInformation signatureInfo = new KeystoreInformation();
            signatureInfo.setKeystoreFilename("certificates.p12");
            signatureInfo.setPassword("test".toCharArray());
            signatureInfo.setType(AS2IntegrationConstants.KEYSTORE_TYPE_PKCS12);
            //the key used to sign outbound AS2 messages is the senders key
            sender.setSignature(signatureInfo, "Key3", AS2IntegrationConstants.SIGNATURE_SHA1);
            //sender.setEncryption(signatureInfo, "Key3", AS2IntegrationConstants.ENCRYPTION_DES); // uncomment to make it work
            AS2RemoteHTTPData response = sender.send();
            if (response.getResponseCode() != -1) {
                AS2MDNParser mdnParser = new AS2MDNParser(response);
                if (mdnParser.isMDN()) {
                    logger.info("Returned HTTP answer is a valid MDN.");
                } else {
                    logger.warning("Returned HTTP answer is no valid MDN.");
                }
            }
            logger.log(Level.ALL, response.toString());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            sender.releaseResources();
        }

    }
}
