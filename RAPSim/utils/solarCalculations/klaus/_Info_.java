package solarCalculations.klaus;

/**
 * Package from: https://github.com/KlausBrunner/solarpositioning
 * 
> Which algorithm should I use?

When in doubt, use SPA. It's widely considered the reference algorithm for solar positioning, being very accurate and usable in a very large time window. Its only downside is that it's relatively slow.
If speed is critical (e.g. you need to calculate lots of positions), consider using PSA. Note however that it's highly optimised for its specified time window (1999-2015), and will be drastically less accurate outside of it.
A fast, yet still accurate alternative would be the Grena/ENEA algorithm, but that's not implemented yet.

> How do I get the time of sunrise/sunset?
Not implemented yet. (Of course you could just search for the time when the zenith angle is 90° by calculating for several times, but that's neither efficient nor elegant.)

 */
public class _Info_ {

}
