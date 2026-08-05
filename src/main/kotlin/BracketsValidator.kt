package org.example

class BracketsValidator {
    companion object {
        private val pairs = mapOf('(' to ')', '[' to ']', '{' to '}')
        private val closers = pairs.values.toSet()

        fun isValid(str: String): Boolean {
            val deque = ArrayDeque<Char>()
            // открыватель - (, [, {
            // закрыватель - ), ], }
            for (char in str) {
                // допустим наш символ это открыватель, то надо выяснить его закрыватель
                // если вместо стандартного символа пришел другой, мы получим null, так как для него нет закрывателя
                val closer = pairs[char]
                when {
                    // char оказался открывателем, потому добавляем его закрыватель в стек
                    closer != null -> deque.addLast(closer)
                    // char оказался закрывателем - проверяем что в стеке последний элемент именно он
                    char in closers -> {
                        if (deque.isEmpty() || deque.removeLast() != char) return false
                    }
                    // наш символ не открыватель и не закрыватель, значит строка не валидная
                    else -> return false
                }
            }
            // проверяем что каждый открыватель закрылся и, следовательно, строка валидная
            return deque.isEmpty()
        }
    }
}