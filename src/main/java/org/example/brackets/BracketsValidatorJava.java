package org.example.brackets;

import java.util.Map;
import java.util.Set;
import java.util.Stack;

public final class BracketsValidatorJava {
    private static final Map<Character, Character> pairs = Map.of('(', ')', '[', ']', '{', '}');
    private static final Set<Character> closers = Set.copyOf(pairs.values());

    private BracketsValidatorJava() {
    }

    /**
     * Проверяет, является ли строка корректной скобочной последовательностью.
     * Контракт совпадает с {@link BracketsValidatorKotlin}.
     *
     * @param str проверяемая строка
     * @return {@code true}, если скобочная последовательность корректна, иначе {@code false}
     */
    public static boolean isValid(String str) {
        var stack = new Stack<Character>();
        for (var ch : str.toCharArray()) {
            var closer = pairs.get(ch);
            if (closer != null) {
                stack.push(closer);
            } else if (closers.contains(ch)) {
                if (stack.isEmpty() || stack.removeLast() != ch) return false;
            } else {
                return false;
            }

        }
        return stack.isEmpty();
    }
}
