 - Para implementar uma cadeia de blocos (*blockchain*) simples com verificação de integridade, foi criada a class Block que representa um bloco da cadeia, com os atributos origem destino, valor e hash.
 - O hash de um bloco foi calculando usando o algoritmo SHA-256 sobre a representação em *String* do vloco anterior, incluindo o seu *hash*. Foi usada a classe *MessageDigest* da **API JCA**.
 - Para armazenar e recuperar a cadeia de blocos dum ficheiro CSV, usamos a classe *FileWriter* para escrever os dados dos blocos em linhas separadas por vírgulas, e a classe *BufferedReader* para ler os dados e criar os objetos *Block* correspondentes.
  - Para adicionar um novo bloco à cadeia, é preciso 
    - receber os dados da transação na linha de comandos, 
    - criar um objeto *Block* com esses dados e o *hash* do último bloco da cadeia, 
    - e, escrever esse objeto no ficheiro CSV.
 - Para validar a integridade da cadeia, é necessário percorrer todos os blocos do ficheiro CSV e verificar se o hash de cada bloco é igual ao hash calculado sobre o bloco anterior. Se algum hash não coincidir, significa que a cadeia foi alterada. 


```terminal
$ java SimpleBlockchain <origin> <destiny> <value> <filename>
```

```terminal
$ java VerifyBlockchain <filename>
```