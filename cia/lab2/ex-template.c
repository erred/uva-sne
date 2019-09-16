
// prime numbers up to %%rax
// eax: prime counter
// r8: outer
// r9: inner
// r10: flag
#define OS3_ASM \
    ""


#include <inttypes.h>
#include <stdio.h>

int main(void) {

uint64_t rax = 0x10;

printf("Before assembly code...\n");
printf("rax: %016" PRIx64 "\n", rax);
printf("\n");

asm volatile (
    OS3_ASM
    : "+a" (rax)
    );

printf("After assembly code...\n");
printf("rax: %016" PRIx64 "\n", rax);

}
