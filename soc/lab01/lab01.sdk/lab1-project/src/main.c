#include "xparameters.h"
#include "xil_io.h"
#include "sleep.h"

#define N              5

/* AXI GPIO registers: GPIO_DATA at +0x0 (16 LEDs, All Outputs) */
#define LED_ADDR       XPAR_AXI_GPIO_0_BASEADDR

#define LED_LD0        0x0001
#define LED_LD1        0x0002

/* SIMULATION is defined by the "Sim" build configuration (-DSIMULATION):
 * short delays for the simulator. The "Debug" configuration builds for the board.
 * usleep(0) never returns on MicroBlaze (loop counter wraps), keep both > 0 */
#ifdef SIMULATION
#define LED_DELAY_US   2U
#define LED_GAP_US     1U
#else
#define LED_DELAY_US   1000000U
#define LED_GAP_US     250000U
#endif

int main(void)
{
    int i, j;

    int A[N][N] = {
        {1, 2, 3, 4, 5},
        {2, 3, 4, 5, 6},
        {3, 4, 5, 6, 7},
        {4, 5, 6, 7, 8},
        {5, 6, 7, 8, 9}
    };

    int B[N][N] = {
        {1, 1, 1, 1, 1},
        {2, 2, 2, 2, 2},
        {3, 3, 3, 3, 3},
        {4, 4, 4, 4, 4},
        {5, 5, 5, 5, 5}
    };

    int C[N][N];

    Xil_Out16(LED_ADDR, 0);

    /* START: LD0 on */
    Xil_Out16(LED_ADDR, LED_LD0);

    for (i = 0; i < N; i++) {
        for (j = 0; j < N; j++) {
            C[i][j] = A[i][j] + B[i][j];
        }
    }

    /* DONE: LD1 on, LD0 off */
    Xil_Out16(LED_ADDR, LED_LD1);
    usleep(LED_DELAY_US);

    /* Show each C[i][j] on LEDs */
    for (i = 0; i < N; i++) {
        for (j = 0; j < N; j++) {
            Xil_Out16(LED_ADDR, (u16)C[i][j]);
            usleep(LED_DELAY_US);
            /* blank between elements so equal neighbours are distinguishable */
            Xil_Out16(LED_ADDR, 0);
            usleep(LED_GAP_US);
        }
    }

    Xil_Out16(LED_ADDR, LED_LD1);

    while (1) {
    }

    return 0;
}
