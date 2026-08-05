package org.example

import java.util.Stack

class BracketsValidator {
    companion object {
        fun isValid(str: String): Boolean {
            val pairs = mapOf(
                '(' to ')',
                '[' to ']',
                '{' to '}'
            )
            val stack = Stack<Char>()
            for (char in str) {
                val opener = pairs.containsKey(char)
                if(!pairs.containsKey(char) && !pairs.containsValue(char)) return false
                if(opener) {
                    val closer = pairs[char]
                    stack.push(closer)
                    continue
                }
                if(stack.isEmpty()) return false
                if(char == stack.lastOrNull()) {
                    stack.pop()
                    continue
                }
            }
            return stack.isEmpty()
        }
    }
}