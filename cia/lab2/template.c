#define OS3_ASM \
"shrq $1, %%rax;" \
"sarq $1, %%rbx;" \
"shlq $1, %%rcx;" \
"salq $1, %%rdx;"

#define ADD_AB "add %%rax, %%rbx;"
#define MULT_BC "imul %%rbx, %%rcx;"
#define SQB_4AB "imul %%rbx, %%rbx;" "imul $4, %%rax;" "imul %%rax, %%rcx;" "sub %%rcx, %%rbx"
#define POLY \
    "mov %%rdx, %%rcx;" \
    "mov $1, %%rax;" \
    "add %%rdx, %%rax;" \
    "imul %%rdx, %%rdx;" \
    "add %%rdx, %%rax;" \
    "imul %%rcx, %%rdx;" \
    "add %%rdx, %%rax;" \
    "imul %%rcx, %%rdx;" \
    "add %%rax, %%rdx;"

// "mov rdx, rbx" // d
// "mov rdx, rax" // d
// "imul rdx rdx" // d2
// "imul rdx rax" // d3
// "add rax rbx" // d+d2
// "add rdx rbx" // d+d2+d3
// "imul rdx rdx" // d4
// "add rbx rdx" // d+d2+d3+d4
// "add $1 rdx"


#include <inttypes.h>

#include <stdio.h>

int main(void) {
    uint64_t rax = 0xFEDCBA9876543210;
    uint64_t rbx = 0x76543210;
    uint64_t rcx = 0x3210;
    uint64_t rdx = 0x10;
    printf("Before assembly code...\n");
    printf("rax: %016" PRIx64 "\n", rax);
    printf("rbx: %016" PRIx64 "\n", rbx);
    printf("rcx: %016" PRIx64 "\n", rcx);
    printf("rdx: %016" PRIx64 "\n", rdx);
    printf("\n");
    asm volatile (
        OS3_ASM
        : "+a" (rax), "+b" (rbx), "+c" (rcx), "+d" (rdx)
    );
    printf("After assembly code...\n");
    printf("rax: %016" PRIx64 "\n", rax);
    printf("rbx: %016" PRIx64 "\n", rbx);
    printf("rcx: %016" PRIx64 "\n", rcx);
    printf("rdx: %016" PRIx64 "\n", rdx);


    printf("add a b \n");
    rax = 0x01;
    rbx = 0x02;
    printf("rax: %016" PRIx64 "\n", rax);
    printf("rbx: %016" PRIx64 "\n", rbx);
    asm volatile ( ADD_AB : "+a" (rax), "+b" (rbx) );
    printf("rax: %016" PRIx64 "\n", rax);
    printf("rbx: %016" PRIx64 "\n", rbx);

    printf("\nmult a b \n");
    rbx = 0x03;
    rcx = 0x07;
    printf("rbx: %016" PRIx64 "\n", rbx);
    printf("rcx: %016" PRIx64 "\n", rcx);
    asm volatile ( MULT_BC : "+b" (rbx), "+c" (rcx) );
    printf("rbx: %016" PRIx64 "\n", rbx);
    printf("rcx: %016" PRIx64 "\n", rcx);

    printf("\nsqb 4ac\n");
    rax = 5;
    rbx = 16;
    rcx = 9;
    printf("rax: %016" PRIx64 "\n", rax);
    printf("rbx: %016" PRIx64 "\n", rbx);
    printf("rcx: %016" PRIx64 "\n", rcx);
    asm volatile ( SQB_4AB: "+a"(rax), "+b"(rbx), "+c"(rcx) );
    printf("rax: %016" PRIx64 "\n", rax);
    printf("rbx: %016" PRIx64 "\n", rbx);
    printf("rcx: %016" PRIx64 "\n", rcx);

    printf("\npoly\n");
    rdx = 16;
    printf("rdx: %016" PRIx64 "\n", rdx);
    asm volatile (POLY: "+d"(rdx));
    printf("rdx: %016" PRIx64 "\n", rdx);

}
