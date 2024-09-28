package gh2;
import edu.princeton.cs.algs4.StdAudio;
import edu.princeton.cs.algs4.StdDraw;

public class GuitarHero {
    private static final String KEYBOARD = "q2we4r5ty7u8i9op-[=zxdcfvgbnjmk,.;/' ";


    public static void main(String[] args) {
        /* create two guitar strings, for concert A and C */
        GuitarString[] strings = new GuitarString[37];
        GuitarString stringPlayed;

        for (int i = 0; i < 37; i += 1) {
            double frequency = 440 * Math.pow(2, (i - 24) / 12);
            strings[i] = new GuitarString(frequency);
        }

        while (true) {

            /* check if the user has typed a key; if so, process it */
            if (StdDraw.hasNextKeyTyped()) {
                char key = StdDraw.nextKeyTyped();
                int index = KEYBOARD.indexOf(key);
                if (index == -1) {
                    continue;
                }
                stringPlayed = strings[index];
                stringPlayed.pluck();
            }

            /* compute the superposition of samples */
            //double sample = stringA.sample() + stringC.sample();
            double sample = 0.;
            for (GuitarString string : strings) {
                sample += string.sample();
            }


            /* play the sample on standard audio */
            StdAudio.play(sample);

            /* advance the simulation of each guitar string by one step */
            for (GuitarString string : strings) {
                string.tic();
            }
        }
    }
}
