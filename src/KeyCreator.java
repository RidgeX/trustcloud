import java.math.BigInteger;
import java.security.*;
import java.security.cert.X509Certificate;
import java.util.*;
import org.bouncycastle.asn1.x500.*;
import org.bouncycastle.asn1.x509.*;
import org.bouncycastle.cert.*;
import org.bouncycastle.cert.jcajce.*;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.operator.*;
import org.bouncycastle.operator.jcajce.*;

public class KeyCreator {
	private static final long MILLISECS_PER_DAY = 24 * 60 * 60 * 1000L;
	
	static {
		Security.addProvider(new BouncyCastleProvider());
	}
	
	public static void main(String[] args) {
		try {
			Date validityBeginDate = new Date(System.currentTimeMillis() - MILLISECS_PER_DAY);
			Date validityEndDate = new Date(System.currentTimeMillis() + 2 * 365 * MILLISECS_PER_DAY);
			
			KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA", "BC");
			keyPairGenerator.initialize(1024, new SecureRandom());
			
			KeyPair keyPair = keyPairGenerator.generateKeyPair();
			PublicKey publicKey = keyPair.getPublic();
			PrivateKey privateKey = keyPair.getPrivate();
			
			X500Name dnName = new X500Name("CN=Test,O=Trustcloud,L=Australia,C=AU");
			BigInteger sn = BigInteger.valueOf(System.currentTimeMillis());
			X509v3CertificateBuilder certGen = new X509v3CertificateBuilder(dnName, sn, validityBeginDate, validityEndDate, dnName, SubjectPublicKeyInfo.getInstance(publicKey.getEncoded()));
			ContentSigner sigGen = new JcaContentSignerBuilder("SHA1WithRSAEncryption").setProvider("BC").build(privateKey);
			X509Certificate cert = new JcaX509CertificateConverter().setProvider("BC").getCertificate(certGen.build(sigGen));
			cert.checkValidity();
			cert.verify(publicKey);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
