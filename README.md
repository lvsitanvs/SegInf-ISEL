# Segurança Informática - Inverno 2023/24

## Informações
### Docente
 - João Pedro Vitorino 
    - [email isel](mailto:joao.vitorino@isel.pt)
    - [email solvit](mailto:joaovitorino@solvit.pt)
### Horário
- 2a-feira, 20:00-23:00: G.2.07 [T] / LS1 (G.0.13)[TP]
- 4a-feira, 18:30-20:00: G.2.07 [T]
### Horário de Apoio
- Presencial / Zoom, 6a-feira, 18:00-18:45
- Agendamento via e-mail
- Link [zoom](https://videoconf-colibri.zoom.us/j/92233712551?pwd=em9mbDl6aTJJWTFxRHdPeXFNWVJXZz09)
- Sala: Sala de reuniões da Solvit (E.0.2)
### Recursos Gerais
- [Turma](https://2324moodle.isel.pt/course/view.php?id=7509)
- [Meta-disciplina](https://2324moodle.isel.pt/course/view.php?id=7503)
### Repositório GitHub
- To Be Anounced
-----------------------------
## Programa
### Parte 1 - Esquemas e protocolos ciptográficos e métodos de gestão de chaves
- esquemas de cifra simétrica e assimétrica
- esquemas MAC e de assinatura digital
- protocolos de autenticação e estabelecimento de chaves
- ifraestruturas de chave pública
### Parte 2  
### Autenticação e autorização
- vulnerabilidades e ataques á informação de autenticação (e.g., *passwords*) e métodos de mitigação
### Modelos e mecanismos para controlo de acessos 
- monitor de referência
- matriz de controlo de acessos
- listas de controlo de acessos e *"capabilities"*
- modelo RBAC (*Role Based Access Control*)
### Protocolos para gestão de identidade e autorização em aplicações Web
-----------------------------------------
## Aulas
## 1. Apresentação e Introdução
> Bibliografia <hr>
> - [01. Apresentação](https://2324moodle.isel.pt/mod/resource/view.php?id=143550)

- **Apresentação**
    - Visão geral
        - 1. Mecanismos e protocolos criptográficos
        - 2. Autenticação e controlo de acessos
    - 2 Trabalhos (40%) + 1 Teste final (60%)
    - Regras de avaliação
- **Introdução à Segurança Informática**
    - Propriedades (Confidencialidade, Integridade, Disponibilidade)
    - Ataques (passivos e ativos)
        - Repasse
            - [Unlocking Car Doors with HackRF Replay Atack](https://www.youtube.com/watch?v=CA3XnGyD-SQ&t=144s)
            - [Relay attack Solihull](https://www.youtube.com/watch?v=8pffcngJJq0&t=20s)
        - Análise de frequência
            - [Frequency Analysis](https://www.cryptool.org/en/cto/frequency-analysis)
- **Introdução à Criptografia**
    - Exemplos clássicos (Cifras de César e Viginière; Máquina Enigma)
        - César
            - [Caesar Cipher](https://en.wikipedia.org/wiki/Caesar_cipher)
        - Viginière
            - [wikipedia](https://en.wikipedia.org/wiki/Vigen%C3%A8re_cipher)
            - [cryptii](https://cryptii.com/pipes/vigenere-cipher)
        - Enigma
            - [wikipedia](https://en.wikipedia.org/wiki/Enigma_machine)
            - [How did the Enigma Machine work? - YouTube](https://www.youtube.com/watch?v=ybkkiGtJmkM)
            - [brilliant](https://brilliant.org/wiki/enigma-machine/)
            - [Cracking Enigma - YouTube](https://www.youtube.com/watch?v=RzWB5jL5RX0)
    - Protocolos, esquemas e primitivas
    - Criptografia Computacional

## 2. Criptografia Simétrica
> Bibliografia <hr> 
> - [02. Esquemas Simétricos](https://2324moodle.isel.pt/mod/resource/view.php?id=143552)
> - Gollmann, Dieter, Computer Security, 3ª edição, 
Wiley, 2011: Secções 14.5.1-2 / 14.3.3
> - Welianng, Du, Computer Security: A Hands-on 
Approach, 2ª edição, 2019: Secções 21.3-5 / 21.4.7, 21.7, 22.5
> - Newsletter: [Schneier](https://www.schneier.com/crypto-gram/)

- **Esquemas Simétricos**
    - Cifra simétrica
        - [Fernet](https://cryptography.io/en/latest/fernet/)
        - [NaCI](https://nacl.cr.yp.to/)
        - [Libsodium](https://doc.libsodium.org/)
        - [João Vitorino - littleCryptoFunTool](https://github.com/vitorinojp/littleCryptoFunTool)
- **Criptografia Simétrica**
    - DES. Princípio / Cifra de Feistel
        - [Data Encryption Standard](https://en.wikipedia.org/wiki/Data_Encryption_Standard)
        - [Feitel Cipher](https://en.wikipedia.org/wiki/Feistel_cipher)
    - Modos de Operação em Bloco
        - *Electronic-Codebook (ECB)*
        - *CipherBlock-chaining (CBC)*
    - Princípios sobre *Initialization Vectors (IV)*
    - Padding
        - Oracle Padding Attack
            - [Security Flaws Induced by CBC Padding](https://www.iacr.org/cryptodb/archive/2002/EUROCRYPT/2850/2850.pdf)
            - [Vulnerabilities in Sogou Keyboard Encryption](https://citizenlab.ca/2023/08/vulnerabilities-in-sogou-keyboard-encryption/)
    - Modos de operação em Fluxo (*Stream*)
        - *Cipher FeedBack (CFB)*
        - *Output FeedBack (OFB)*
        - *Counter (CTR)* 
- **Message Authenticartion Codes (MAC)**
    - Propriedades
    - *MAC-then-encrypt vs Encrypt-then-MAC vs MAC-and-encrypt*
        - [Authenticated Encryption: Relations amoung Notions and Analysis of the Generic Composition Paradigm](https://link.springer.com/content/pdf/10.1007/3-540-44448-3_41.pdf)
- **Modos de Cifra Autenticada**
    - *Galois Counter Mode (GCM)*


