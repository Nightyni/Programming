#define _CRT_SECURE_NO_WARNINGS
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <math.h>
#include <ctype.h>

#define MAX_EXPR_LENGTH 100
#define STACK_INIT_SIZE 100
#define STACKINCREMENT 10

// 表达式类型枚举
typedef enum {
    TAUTOLOGY,
    CONTRADICTION,
    CONTINGENT
} ExpressionType;

// 逻辑表达式结构体
typedef struct {
    char original_expr[MAX_EXPR_LENGTH];  // 原始表达式
    char processed_expr[MAX_EXPR_LENGTH]; // 处理后的表达式
    int variable_values[26];              // 变量值(A-Z对应0-25)
    int variables[26];                    // 标记哪些变量存在于表达式中
    int var_count;                        // 变量数量
    char var_list[26];                    // 变量列表
} LogicExpression;

// 栈结构定义
typedef struct {
    char* base;
    char* top;
    int stacksize;
} SqStack;

// 栈初始化
int InitStack(SqStack* s) {
    s->base = (char*)malloc(STACK_INIT_SIZE * sizeof(char));
    if (!s->base) return 0;
    s->top = s->base;
    s->stacksize = STACK_INIT_SIZE;
    return 1;
}

// 获取栈顶元素
char GetTop(SqStack* s) {
    if (s->top == s->base) return '\0';
    return *(s->top - 1);
}

// 压栈
int Push(SqStack* s, char e) {
    if (s->top - s->base >= s->stacksize) {
        s->base = (char*)realloc(s->base, (s->stacksize + STACKINCREMENT) * sizeof(char));
        if (!s->base) return 0;
        s->top = s->base + s->stacksize;
        s->stacksize += STACKINCREMENT;
    }
    *s->top++ = e;
    return 1;
}

// 出栈
char Pop(SqStack* s) {
    if (s->top == s->base) return '\0';
    return *--s->top;
}

// 判断运算符优先级
char Precede(char a, char b) {
    if (a == '|') {
        if (b == '|' || b == ')' || b == '#') return '>';
        return '<';
    }
    else if (a == '&') {
        if (b == '|' || b == '&' || b == ')' || b == '#') return '>';
        return '<';
    }
    else if (a == '~') {
        if (b == '|' || b == '&' || b == '~' || b == ')' || b == '#') return '>';
        return '<';
    }
    else if (a == '(') {
        if (b == ')') return '=';
        return '<';
    }
    else if (a == '#') {
        if (b == '#') return '=';
        return '<';
    }
    return ' '; 
}

// 执行逻辑运算
int Operate(int a, char theta, int b) {
    switch (theta) {
    case '&': return a && b;
    case '|': return a || b;
    case '~': return !a;
    default: return 0;
    }
}

// 验证表达式合法性
int ValidateExpression(const char* expr) {
    int paren_count = 0;
    int last_was_operator = 1; // 开始时期望操作数
    int has_variables = 0;

    for (int i = 0; expr[i] != '\0'; i++) {
        if (isspace(expr[i])) continue;

        if (expr[i] == '(') {
            paren_count++;
            last_was_operator = 1;
        }
        else if (expr[i] == ')') {
            if (paren_count == 0) {
                printf("Error: Unmatched closing parenthesis\n");
                return 0;
            }
            paren_count--;
            last_was_operator = 0;
        }
        else if (strchr("|&~", expr[i])) {
            if (last_was_operator && expr[i] != '~') {
                printf("Error: Consecutive operators\n");
                return 0;
            }
            last_was_operator = 1;
        }
        else if (isupper(expr[i])) {
            last_was_operator = 0;
            has_variables = 1;
        }
        else if (!strchr("#", expr[i])) {
            printf("Error: Invalid character '%c'\n", expr[i]);
            return 0;
        }
    }

    if (paren_count != 0) {
        printf("Error: Unmatched opening parenthesis\n");
        return 0;
    }

    if (!has_variables) {
        printf("Error: Expression must contain at least one variable\n");
        return 0;
    }

    return 1;
}

// 提取表达式中的变量
void ExtractVariables(LogicExpression* expr) {
    expr->var_count = 0;
    for (int i = 0; i < 26; i++) {
        expr->variables[i] = 0;
        expr->var_list[i] = '\0';
    }

    for (int i = 0; expr->processed_expr[i] != '\0'; i++) {
        if (isupper(expr->processed_expr[i])) {
            int idx = expr->processed_expr[i] - 'A';
            if (!expr->variables[idx]) {
                expr->variables[idx] = 1;
                expr->var_list[expr->var_count++] = 'A' + idx;
            }
        }
    }
}

// 表达式求值
int EvaluateExpression(LogicExpression* expr) {
    SqStack optr, opnd;
    int i = 0;
    char c, theta;
    int a, b, result;

    InitStack(&optr);
    Push(&optr, '#');
    InitStack(&opnd);

    c = expr->processed_expr[i++];
    while (c != '#' || GetTop(&optr) != '#') {
        if (isupper(c)) { // 变量
            Push(&opnd, expr->variable_values[c - 'A'] + '0');
            c = expr->processed_expr[i++];
        }
        else if (c == '0' || c == '1') { // 常量
            Push(&opnd, c);
            c = expr->processed_expr[i++];
        }
        else { // 运算符
            switch (Precede(GetTop(&optr), c)) {
            case '<':
                Push(&optr, c);
                c = expr->processed_expr[i++];
                break;
            case '=':
                Pop(&optr);
                c = expr->processed_expr[i++];
                break;
            case '>':
                theta = Pop(&optr);
                if (theta != '~') {
                    b = Pop(&opnd) - '0';
                }
                else {
                    b = 0; // 对于~操作符，只需要一个操作数
                }
                a = Pop(&opnd) - '0';
                result = Operate(a, theta, b);
                Push(&opnd, result + '0');
                break;
            }
        }
    }

    result = GetTop(&opnd) - '0';
    free(optr.base);
    free(opnd.base);
    return result;
}

// 显示真值表
void show_truth_table(LogicExpression* expr) {
    int total_combinations = 1 << expr->var_count;

    // 打印表头
    printf("\nTruth Table:\n");
    for (int i = 0; i < expr->var_count; i++) {
        printf("%c ", expr->var_list[i]);
    }
    printf("| Result\n");

    // 打印分隔线
    for (int i = 0; i < expr->var_count * 2 + 8; i++) {
        printf("-");
    }
    printf("\n");

    // 生成并打印所有组合
    for (int i = 0; i < total_combinations; i++) {
        // 设置变量值
        for (int j = 0; j < expr->var_count; j++) {
            int idx = expr->var_list[j] - 'A';
            expr->variable_values[idx] = (i >> (expr->var_count - j - 1)) & 1;
            printf("%d ", expr->variable_values[idx]);
        }

        // 计算并打印结果
        int result = EvaluateExpression(expr);
        printf("|   %d\n", result);
    }
}

// 检查表达式类型
ExpressionType CheckTautology(LogicExpression* expr) {
    int total_combinations = 1 << expr->var_count;
    int always_true = 1, always_false = 1;

    for (int i = 0; i < total_combinations; i++) {
        // 设置变量值
        for (int j = 0; j < expr->var_count; j++) {
            int idx = expr->var_list[j] - 'A';
            expr->variable_values[idx] = (i >> j) & 1;
        }

        // 评估表达式
        int result = EvaluateExpression(expr);

        if (result) {
            always_false = 0;
        }
        else {
            always_true = 0;
        }

        // 如果既不是重言式也不是矛盾式，可以提前退出
        if (!always_true && !always_false) break;
    }

    if (always_true) return TAUTOLOGY;
    if (always_false) return CONTRADICTION;
    return CONTINGENT;
}

// 预处理表达式
int PreprocessExpression(LogicExpression* expr, const char* input) {
    int i, j = 0;

    // 复制原始表达式
    strncpy(expr->original_expr, input, MAX_EXPR_LENGTH);

    for (i = 0; input[i] != '\0'; i++) {
        if (isspace(input[i])) continue;

        // 处理多位运算符
        if (input[i] == '|' && input[i + 1] == '|') {
            expr->processed_expr[j++] = '|';
            i++;
        }
        else if (input[i] == '&' && input[i + 1] == '&') {
            expr->processed_expr[j++] = '&';
            i++;
        }
        else {
            expr->processed_expr[j++] = input[i];
        }
    }
    expr->processed_expr[j++] = '#';//添加结束标记
    expr->processed_expr[j] = '\0';

    return ValidateExpression(expr->processed_expr);
}

// 交互模式：用户输入变量值
void interactive_mode(LogicExpression* expr) {
    printf("Enter values for variables (0 or 1):\n");
    for (int i = 0; i < expr->var_count; i++) {
        char var = expr->var_list[i];
        printf("%c = ", var);

        // 使用更安全的输入方式
        int value;
        while (scanf("%d", &value) != 1 || (value != 0 && value != 1)) {
            // 清空错误输入
            while (getchar() != '\n');
            printf("Please enter 0 or 1 for %c: ", var);
        }
        expr->variable_values[var - 'A'] = value;
    }

    int result = EvaluateExpression(expr);
    printf("Result: %d\n", result);

    // 清空输入缓冲区中的换行符
    while (getchar() != '\n');
}


int main() {
    char input[MAX_EXPR_LENGTH];
    int count, i;

    printf("Enter the number of test cases: ");
    if (scanf("%d", &count) != 1 || count <= 0) {
        printf("Error: Invalid number of test cases\n");
        return 1;
    }

    // 清空缓冲区
    int c;
    while ((c = getchar()) != '\n' && c != EOF);

    for (i = 0; i < count; i++) {
        printf("Enter logical expression %d: ", i + 1);
        if (!fgets(input, MAX_EXPR_LENGTH, stdin)) {
            printf("Error reading input\n");
            // 清空缓冲区后继续
            while ((c = getchar()) != '\n' && c != EOF);
            continue;
        }
        input[strcspn(input, "\n")] = 0;

        LogicExpression expr;
        if (!PreprocessExpression(&expr, input)) {
            printf("Invalid expression. Please try again.\n");
            continue;
        }

        ExtractVariables(&expr);
        ExpressionType type = CheckTautology(&expr);

        switch (type) {
        case TAUTOLOGY:
            printf("True forever\n");
            break;
        case CONTRADICTION:
            printf("False forever\n");
            break;
        case CONTINGENT:
            printf("Satisfactible\n");
            printf("Variables: ");
            for (int j = 0; j < expr.var_count; j++) {
                printf("%c ", expr.var_list[j]);
            }
            printf("\n");

            if (expr.var_count <= 4) {
                show_truth_table(&expr);
            }
            else {
                printf("Too many variables (%d) for full truth table display.\n", expr.var_count);
            }

            interactive_mode(&expr);
            break;
        }

        if (i < count - 1) printf("\n");
    }

    return 0;
}