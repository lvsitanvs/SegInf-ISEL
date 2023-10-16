- 7a) Implementar um processo completo de proteção de texto usando a biblioteca Java Cryptography Architecture (JCA) e representar o resultado no formato JSON Web Encryption (JWE) é um projeto complexo. Abordarei cada parte do processo passo a passo.
Primeiro, vamos criar um componente para cifrar e decifrar strings usando o algoritmo AES em modo GCM. Vamos dividir o código em duas partes: uma para cifrar e outra para decifrar. Certifique-se de que você possui as bibliotecas necessárias para manipular chaves e certificados em Java. Para este exemplo, usaremos Bouncy Castle para lidar com criptografia assimétrica e segurança.

        execice7.AESEncryptation.java - componente de cifragem e decifragem AES-GCM.

        execice7.JWEProcessor - pode cifrar uma mensagem usando uma chave simétrica AES-GCM, cifrar a chave simétrica usando criptografia assimétrica e criar um JWE. Também pode decifrar um JWE, desenrolar a chave simétrica e decifrar a mensagem.


- 7B) Para cifrar e decifrar uma chave simétrica AES usando o algoritmo RSA, você pode seguir o exemplo abaixo. Nesse exemplo, usaremos a criptografia RSA para proteger a chave AES e, em seguida, usaremos essa chave para cifrar e decifrar o texto. 

        RCAKeyEncryption - geramos uma chave simétrica AES e um par de chaves RSA. Em seguida, usamos a chave pública RSA para cifrar a chave simétrica e a chave privada RSA para decifrá-la. Por fim, ciframos e deciframos um texto usando a chave simétrica para testar a funcionalidade


- 7c) Para obter a chave pública de um certificado X.509 validado e uma chave privada de um Keystore em Java, usamos a biblioteca Java KeyStore para manipular o Keystore e as classes Java padrão para carregar e validar um certificado X.509.

        execice7.KeyStoreAndCertificate
        - getPublicKeyFromCertificate: Carrega um certificado X.509 de um arquivo e extrai a chave pública do certificado.

        - getPrivateKeyFromKeystore: Carrega um Keystore de um arquivo, acessa uma entrada pelo alias e obtém a chave privada protegida pela senha fornecida.

- 7d) TODO: test and implement