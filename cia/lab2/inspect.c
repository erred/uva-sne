#include <stdio.h>
#include <inttypes.h>

// counts the number of primes <= d
// in: d
// out: d
#define PRIMECOUNT \
"mov $0, %%rcx;" \
"mov %%rdx, %%r9;" \
"mov $2, %%r10;" \
"startouter:" \
"cmp %%r9, %%r10;" \
"jg endouter;" \
"mov $2, %%r11;" \
"startinner:" \
"cmp %%r10, %%r11;" \
"jge endinner;" \
"mov %%r10, %%rax;" \
"mov $0, %%rdx;" \
"div %%r11;" \
"cmp $0, %%rdx;" \
"je notprime;" \
"add $1, %%r11;" \
"jmp startinner;" \
"endinner:" \
"add $1, %%rcx;" \
"notprime:" \
"add $1, %%r10;" \
"jmp startouter;" \
"endouter:" \
"mov %%rcx, %%rdx;" 


int main(void) {
    uint64_t d = 10;
    printf("rdx: %016" PRIx64 "\n", d);
    asm volatile( PRIMECOUNT : "+d"(d) );
    printf("rdx: %016" PRIx64 "\n", d);

}

//
// int assembly(int d){
//     int c = 0;
//     for(int r10 = 2; r10 <= d; r10++){
//         for(int r11 = 2; r11 < r10; r11++) {
//             if(r10 % r11 == 0) {
//                 goto notprime;
//             }
//         }
//         c++;
// notprime:
//     }
//     return c;
// }
//
// mov $0, %%rcx;
//
// mov $2, %%r10;
// startouter:
// cmp %%r10, %%rdx;
// jg endouter;
//
// mov $2, %%r11;
// startinner:
// cmp %%r11, %%r10;
// jge endinner;
//
// mov %%r10, %%rax;
// div %%r11;
// cmp $0, %%rax;
// je notprime;
//
// add $1, %%r11
// jmp startinner;
//
// endinner:
// add $1, %%rcx;
// notprime:
// add $1, %%r10;
// jmp startouter;
//
// endouter:
// mov %%rcx, %%rdx;
