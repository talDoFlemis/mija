# Parser 

## Tokens

### Punctuation
| Type       | Examples |     | Type        | Examples |
| ---------- | -------- | --- | ----------- | -------- |
| `LSQUIRLY` | `{`      |     | `RSQUIRLY`  | `}`      |
| `LPAREN`   | `(`      |     | `RPAREN`    | `)`      |
| `LBRACKET` | `[`      |     | `RBRACKET`  | `]`      |
| `COMMA`    | `,`      |     | `SEMICOLON` | `;`      |
| `DOT`      | `.`      |     |             |          |

### Keywords

| Type     | Examples             |     | Type      | Examples  |
| -------- | -------------------- | --- | --------- | --------- |
| `CLASS`  | `class`              |     | `PUBLIC`  | `public`  |
| `STATIC` | `static`             |     | `VOID`    | `void`    |
| `MAIN`   | `main`               |     | `EXTENDS` | `extends` |
| `RETURN` | `return`             |     | `STRING`  | `string`  |
| `IF`     | `if`                 |     | `ELSE`    | `else`    |
| `WHILE`  | `while`              |     | `THIS`    | `this`    |
| `NEW`    | `new`                |     | `LENGTH`  | `length`  |
| `SOUT`   | `System.out.println` |     |           |           |

### Types

| Type        | Examples  |
| ----------- | --------- |
| `INT`       | `int`     |
| `INT_ARRAY` | `int[]`   |
| `BOOLEAN`   | `boolean` |

### Literals

| Type              | Examples           |
| ----------------- | ------------------ |
| `TRUE_LITERAL`    | `true`             |
| `FALSE_LITERAL`   | `false`            |
| #DIGIT            | `1`, `2`, `3`      |
| #LETTER           | `gabrigas`, `said` |
| `IDENTIFIER`      | `gabs`, `saidx30`  |
| `INTEGER_LITERAL` | `762`, `21`        |

### Operators

| Type    | Examples |
| ------- | -------- |
| `EQ`    | `=`      |
| `AND`   | `&&`     |
| `LT`    | `<`      |
| `PLUS`  | `+`      |
| `MINUS` | `-`      |
| `STAR`  | `*`      |
| `BANG`  | `!`      |


## Statements
> **N***, where N is a nonterminal, to mean 0, 1, or a more repetitions of N.

- `{ Statement* }`
- `if (Exp) Statement else Statement` 
- `while (Exp) Statement`
- `System.out.println (Exp);`
- `id = Exp;`
- `id [ Exp ] = Exp;`

## Expressions

- `Exp op Exp`
- `Exp [Exp]`
- `Exp . length`
- `Exp . id ( ExpList  )`
- INTEGER_LITERAL
- **true**
- **false**
- id
- **this**
- **new int** [ Exp ]
- **new** id ( )
- ! Exp
- ( Exp )
- ExpList:
  - Exp ExpRest* 
    - ExpRest: , Exp


## Usubility

- `Program` is the start symbol

MainClass ClassDecl
- `MainClass`:

**class** id { **public static void main** ( **String** [] id )
{ Statement } }

- `ClassDecl`:

**class** id { VarDecl∗ MethodDecl∗ }
**class** id **extends** id { VarDecl ∗ MethodDecl∗ }

- `VarDecl`:

Type id;

- `MethodDecl`:

**public** Type id ( FormalList ) { VarDecl∗ Statement∗ **return** Exp; }

- `FormalList`:

Type id FormalRest∗

- `FormalRest`:

, Type id


## Example

```java
class Factorial {
    public static void main(String[] a) {
        System.out.println(new Fac().ComputeFac(10));
    }
}
class Fac {
    public int ComputeFac(int num) {
        int num_aux;
        if (num < 1)
        num_aux = 1;
        else
            num_aux = num * (this.ComputeFac(num-1));
        return num_aux;
    }
}
```

