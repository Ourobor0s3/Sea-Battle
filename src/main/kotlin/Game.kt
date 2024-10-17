import Entities.Enum.StatusCell
import Entities.Model.GameBoard
import Entities.Model.Player
import Players.PlayerComputer
import Players.PlayerHuman

class Game {
    private val playerHuman = PlayerHuman(GameBoard())
    private val playerComputer = PlayerComputer(GameBoard())
    private var isGameOver = false

    fun start() {
        println("Добро пожаловать в Морской бой!")

        playerHuman.placeShips()
        playerComputer.placeShips()

        isGameOver = false
        while (!isGameOver) {
            playTurn(playerHuman, playerComputer, isHumanTurn = true)
            if (isGameOver) break
            playTurn(playerComputer, playerHuman, isHumanTurn = false)
        }
        println("Игра окончена!")
    }

    private fun playTurn(currentPlayer: Player, opponentPlayer: Player, isHumanTurn: Boolean) {
        var continueTurn = true
        while (continueTurn && !isGameOver) {
            if (isHumanTurn) {
                printBoards()
            }

            val move = currentPlayer.makeMove(opponentPlayer.gameBoard)
            if (!opponentPlayer.gameBoard.isValidMove(move)) {
                println("Неверный ход. Попробуйте снова.")
                continue
            }

            val result = opponentPlayer.gameBoard.receiveShot(move)

            currentPlayer.processShotResult(opponentPlayer.gameBoard, move, result)
            continueTurn = result == StatusCell.HIT

            checkGameOver(opponentPlayer)
        }
    }

    private fun checkGameOver(player: Player) {
        isGameOver = player.gameBoard.grid.flatten().none { it.status == StatusCell.SHIP }
        if (isGameOver) {
            println("${player::class.simpleName} проиграл! Все корабли потоплены.")
        }
    }

    // Вывод досок игрока и компьютера
    private fun printBoards() {
        println("\tВаша доска:\t\t\t\tДоска компьютера:")
        print("   ")
        for (i in 0 until playerHuman.gameBoard.size) {
            print("$i ")
        }
        print("\t\t   ")
        for (i in 0 until playerComputer.gameBoard.size) {
            print("$i ")
        }
        println()

        for (i in 0 until playerHuman.gameBoard.size) {
            print("$i  ")
            printRow(playerHuman.gameBoard, i, true)
            print("\t\t")
            print("$i  ")
            printRow(playerComputer.gameBoard, i, false)
            println()
        }
    }

    // Вывод строки доски
    private fun printRow(gameBoard: GameBoard, row: Int, showShips: Boolean) {
        for (cell in gameBoard.grid[row]) {
            val symbol = cell.getStat(showShips)
            print("$symbol ")
        }
    }
}
