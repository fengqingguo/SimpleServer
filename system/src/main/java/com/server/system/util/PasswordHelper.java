package com.server.system.util;

import com.server.system.pojo.User;
import org.apache.shiro.crypto.RandomNumberGenerator;
import org.apache.shiro.crypto.SecureRandomNumberGenerator;
import org.apache.shiro.crypto.hash.SimpleHash;
import org.apache.shiro.util.ByteSource;
import org.springframework.stereotype.Component;

@Component
public class PasswordHelper {
    private RandomNumberGenerator randomNumberGenerator = new SecureRandomNumberGenerator();
    public String algorithmName = "md5";
    public int hashIterations = 2;

    public void setRandomNumberGenerator(RandomNumberGenerator randomNumberGenerator) {
        this.randomNumberGenerator = randomNumberGenerator;
    }

    public void setAlgorithmName(String algorithmName) {
        this.algorithmName = algorithmName;
    }

    public void setHashIterations(int hashIterations) {
        this.hashIterations = hashIterations;
    }

    public void encryptPassword(User user) {
    	if(user.getPassword()!=null){
    		user.setSalt(randomNumberGenerator.nextBytes().toHex());
    		String newPassword = new SimpleHash(algorithmName, user.getPassword(), ByteSource.Util.bytes(user
    				.getCredentialsSalt()), hashIterations).toHex();
    		user.setPassword(newPassword);
    	}
    }
}
