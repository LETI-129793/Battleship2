# Refactoring Opportunities

## Critérios de seleção

A Parte 2, A.2 do guião pede uma tabela com quatro colunas: localização (`classe` ou `classe::método`), nome do cheiro no código, nome da refabricação e número do aluno. A secção A.3 pede que as oportunidades sejam distribuídas de forma a que cada elemento aplique, no seu ramo, **entre 5 e 10 refabricações de tipos diferentes** (o mesmo tipo pode repetir-se entre alunos, em locais diferentes).


## Tabela de oportunidades

| Local (`classe` ou `classe::método`) | Nome do cheiro no código | Nome da refabricação | Número do aluno |
| :--- | :--- | :--- | :--- |
| `Move::processEnemyFire` | **Long Method** — ~140 linhas que contam os resultados, constroem a mensagem para a consola e constroem o JSON | **Extract Method** — extrair a construção da mensagem *verbose* (`buildSummary`) e a construção do mapa de resposta (`buildResponse`) | 129785 |
| `Move` | **Large Class / Divergent Change** — a classe de domínio também trata da serialização JSON com Jackson (`ObjectMapper`) | **Extract Class** — mover a serialização dos resultados para uma nova classe (ex.: `MoveResultSerializer`) | 129785 |
| `Move::getShots` / `Move::getShotResults` | **Exposed Internal State** — devolvem diretamente as listas internas mutáveis | **Encapsulate Collection** — devolver `List.copyOf(...)` / `Collections.unmodifiableList(...)` | 129785 |
| `Position::Position(char, int)` | **Constructor ambíguo** — dois construtores com assinaturas parecidas (`(char,int)` e `(int,int)`) e significados diferentes (coordenadas clássicas vs. índices) | **Replace Constructor with Factory Method** — criar `Position.fromClassic(char, int)` | 129785 |
| `Position` (campos `isOccupied`, `isHit`) | **Nomes enganadores** — os campos têm o mesmo nome que os métodos `isOccupied()` / `isHit()` | **Rename Field** — renomear para `occupied` e `hit` | 129785 |
| `Position::adjacentPositions` | **Magic Number / Constant Expression** — a matriz `directions` é um literal recriado em cada chamada | **Introduce Constant** — extrair para `private static final int[][] DIRECTIONS` | 129785 |
| `MoveRepository::saveMove` | **Expressão repetida** — `result.ship()` é avaliado duas vezes em `result.ship() == null ? null : result.ship().getCategory()` | **Introduce Variable** — introduzir `IShip ship = result.ship()` | 129785 |
| `MoveRepository` (records `StoredShot`, `StoredMove`) | **Large Class** — a classe de acesso a dados também declara os DTOs públicos usados noutras classes | **Move Inner Class to Upper Level** — passar os records para ficheiros próprios | 129785 |
| `Caravel`, `Carrack`, `Frigate` (construtores) | **Duplicated Code em subclasses** — o mesmo `switch (bearing)` com os mesmos ciclos repetido em três subclasses | **Pull Up Method** — extrair o preenchimento linear das posições e subi-lo para `Ship` | 129780 |
| `Ship::getTopMostPos`, `getBottomMostPos`, `getLeftMostPos`, `getRightMostPos` | **Duplicated Code** — quatro métodos quase iguais que só mudam a coordenada e o operador de comparação | **Extract Method** — método auxiliar parametrizado (coordenada + comparador) | 129780 |
| `Ship::Ship` (construtor) | **Redundant Assignment** — `category`, `bearing` e `pos` são atribuídos duas vezes (com `requireNonNull` e logo a seguir sem) | **Remove Dead Code** — remover as atribuições repetidas | 129780 |
| `Ship` (campo `positions`) | **Incorrect Visibility** — campo `protected`, embora as subclasses só lhe acedam através de `getPositions()` | **Change Visibility** — passar o campo para `private` | 129780 |
| `IShip::getSize` / `Ship` (campo `size`) | **Primitive wrapper desnecessário** — `Integer` usado onde `int` basta (*boxing* desnecessário) | **Type Migration** — migrar `Integer` para `int` | 129780 |
| `Fleet::colisionRisk` | **Nome com erro ortográfico** — "colision" em vez de "collision" | **Rename Method** — renomear para `collisionRisk` | 129780 |
| `Fleet::colisionRisk` | **Ciclo imperativo** — ciclo com índice e `return` antecipado | **Replace Loop with Pipeline** — `ships.stream().anyMatch(ship -> ship.tooCloseTo(s))` | 129780 |
| `Fleet::createRandom` | **Magic Values** — composição da frota definida num array literal dentro do método | **Introduce Constant** — extrair para `private static final String[] FLEET_COMPOSITION` | 129780 |
| `Tasks::menu` | **Long Method / Switch Statement** — `switch` com ~100 linhas e a lógica de cada comando dentro dos `case` | **Extract Method** — extrair um método por comando (ex.: `handleRajada`, `handleSimula`, `handlePdf`) | 129793 |
| `Tasks::menu` (`case "pdf"`) | **Magic String** — usa o literal `"pdf"` embora a constante `PDF` já exista | **Replace with Constant** — substituir o literal por `PDF` | 129793 |
| `Game::fireSingleShot` | **Expressão repetida** — `!ship.stillFloating()` é avaliado duas vezes | **Introduce Variable** — introduzir `boolean sunk = !ship.stillFloating()` | 129793 |
| `Game::getRemainingShips` | **Redundant Temporary Variable** — a variável `floatingShips` só é usada na linha seguinte | **Inline Variable** — `return myFleet.getFloatingShips().size()` | 129793 |
| `Game::printBoard` | **Flag Arguments / Long Parameter List** — dois `boolean` (`show_shots`, `showLegend`) que controlam o comportamento | **Introduce Parameter Object** — agrupar as opções de visualização num objeto (ex.: `BoardView`) | 129793 |
| `Game::printBoard`, `printMyBoard`, `printAlienBoard`, `printShips` | **Naming Convention** — parâmetros e variáveis em *snake_case* (`show_shots`, `show_legend`, `ship_pos`, `adjacent_pos`) | **Rename** — passar para *camelCase* (`showShots`, `showLegend`, …) | 129793 |
| `Tasks::readClassicPosition` | **Feature Envy / Cyclic Dependency** — o método só constrói uma `Position` e é chamado por `Game::readEnemyFire` (`Game` → `Tasks` → `Game`) | **Move Method** — mover para `Position` (ex.: `Position.parseClassic(Scanner)`) | 129793 |
| `Game::repeatedShot` | **Ciclo imperativo** — ciclo aninhado com `return true` antecipado | **Replace Loop with Pipeline** — `alienMoves.stream().anyMatch(m -> m.getShots().contains(pos))` | 129793 |

## Resumo por aluno

| Aluno | Refabricações (8 tipos diferentes) |
| :--- | :--- |
| 129785 | Extract Method, Extract Class, Encapsulate Collection, Replace Constructor with Factory Method, Rename Field, Introduce Constant, Introduce Variable, Move Inner Class to Upper Level |
| 129780 | Pull Up Method, Extract Method, Remove Dead Code, Change Visibility, Type Migration, Rename Method, Replace Loop with Pipeline, Introduce Constant |
| 129793 | Extract Method, Replace with Constant, Introduce Variable, Inline Variable, Introduce Parameter Object, Rename, Move Method, Replace Loop with Pipeline |

