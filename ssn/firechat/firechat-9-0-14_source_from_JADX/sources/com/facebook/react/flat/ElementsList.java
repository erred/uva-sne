package com.facebook.react.flat;

import java.lang.reflect.Array;
import java.util.ArrayDeque;
import java.util.ArrayList;

final class ElementsList<E> {
    private Scope mCurrentScope = null;
    private final ArrayDeque<E> mElements = new ArrayDeque<>();
    private final E[] mEmptyArray;
    private int mScopeIndex = 0;
    private final ArrayList<Scope> mScopesStack = new ArrayList<>();

    private static final class Scope {
        Object[] elements;
        int index;
        int size;

        private Scope() {
        }
    }

    public ElementsList(E[] eArr) {
        this.mEmptyArray = eArr;
        this.mScopesStack.add(this.mCurrentScope);
    }

    public void start(Object[] objArr) {
        pushScope();
        Scope currentScope = getCurrentScope();
        currentScope.elements = objArr;
        currentScope.index = 0;
        currentScope.size = this.mElements.size();
    }

    public E[] finish() {
        E[] eArr;
        Scope currentScope = getCurrentScope();
        popScope();
        int size = this.mElements.size() - currentScope.size;
        if (currentScope.index != currentScope.elements.length) {
            eArr = extractElements(size);
        } else {
            for (int i = 0; i < size; i++) {
                this.mElements.pollLast();
            }
            eArr = null;
        }
        currentScope.elements = null;
        return eArr;
    }

    public void add(E e) {
        Scope currentScope = getCurrentScope();
        if (currentScope.index >= currentScope.elements.length || currentScope.elements[currentScope.index] != e) {
            currentScope.index = Integer.MAX_VALUE;
        } else {
            currentScope.index++;
        }
        this.mElements.add(e);
    }

    public void clear() {
        if (getCurrentScope() != null) {
            throw new RuntimeException("Must call finish() for every start() call being made.");
        }
        this.mElements.clear();
    }

    private E[] extractElements(int i) {
        if (i == 0) {
            return this.mEmptyArray;
        }
        E[] eArr = (Object[]) Array.newInstance(this.mEmptyArray.getClass().getComponentType(), i);
        for (int i2 = i - 1; i2 >= 0; i2--) {
            eArr[i2] = this.mElements.pollLast();
        }
        return eArr;
    }

    private void pushScope() {
        this.mScopeIndex++;
        if (this.mScopeIndex == this.mScopesStack.size()) {
            this.mCurrentScope = new Scope();
            this.mScopesStack.add(this.mCurrentScope);
            return;
        }
        this.mCurrentScope = (Scope) this.mScopesStack.get(this.mScopeIndex);
    }

    private void popScope() {
        this.mScopeIndex--;
        this.mCurrentScope = (Scope) this.mScopesStack.get(this.mScopeIndex);
    }

    private Scope getCurrentScope() {
        return this.mCurrentScope;
    }
}
