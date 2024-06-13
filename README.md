# Mija

![Build Status](https://img.shields.io/github/actions/workflow/status/taldoflemis/mija/gradle.yml)
![Issues](https://img.shields.io/github/issues/taldoflemis/mija)
![Pull requests](https://img.shields.io/github/issues-pr/taldoflemis/mija)

A **Mi**ni**Ja**va compiler implementation

## Relatório

### Status

- Analisador Léxico e sintático feito com JavaCC e ANTLR
- Árvore Sintática Abstrata e Analisador Semântico feitos
- Visitor do output da AST em Mermaid
- Tradução para código intermediário do livro, LLVM e byte-code
- Tradução do código intermediário do livro para o canônico
- Seleção de instruções parcialmente feita

### Instruções MIPS usadas

| Instrução |                                                          Explicação                                                          |
| :-------: | :--------------------------------------------------------------------------------------------------------------------------: |
|    ADD    | É útil para cálculos aritméticos básicos e operações que exigem a soma de valores que já estão armazenados em registradores. |
|   ADDI    |                                  É eficiente para incrementos constantes e ajustes rápidos.                                  |
|    SUB    |             É útil para cálculos que exigem a diferença entre valores dois valores armazenados em registradores.             |
|   SUBI    |                                   É prático para decrementos constantes e ajustes rápidos.                                   |
|    MUL    |                   É essencial para operações que exigem produtos de valores armazenados em registradores.                    |
|    DIV    |                      É útil para cálculos que exigem divisões com valores armazenados em registradores.                      |
|   LOAD    |          É essencial para acessar dados armazenados na memória durante a execução de um programa em um registrador.          |
|   STORE   |                    É fundamental para salvar resultados intermediários ou finais de cálculos em memória.                     |
|   MOVEM   |         É útil para reorganizar dados dentro da memória, sem utilizar um passo extra de LOAD para outro registrador.         |
|   JUMP    |                                   Necessário para fazer o Jump incondicional de instruções                                   |
|    BGE    |                               Útil para branch se o valor de um registrador é maior que outro                                |
|    BLT    |                               Útil para branch se o valor de um registrador é menor que outro                                |
|    BEQ    |                                Útil para branch se o valor de um registrador é igual ao outro                                |
|    BNE    |                              Útil para branch se o valor de um registrador não é igual ao outro                              |
|   CALL    |                                         Instrução necessária para chamada de função                                          |

Referência: https://pages.cpsc.ucalgary.ca/~robin/class/510/jouette.pdf
