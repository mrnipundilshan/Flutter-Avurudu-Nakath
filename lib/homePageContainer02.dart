import 'package:flutter/material.dart';
import 'homePageContainer01.dart'; // Import to reuse the getNextEvent function

class HomePageContainer02 extends StatelessWidget {
  final String days;
  final String hours;
  final String minutes;
  final String seconds;
  final VoidCallback onTap;

  const HomePageContainer02({
    super.key,
    required this.days,
    required this.hours,
    required this.minutes,
    required this.seconds,
    required this.onTap,
    required int eventId,
  });

  @override
  Widget build(BuildContext context) {
    // Get the next upcoming event using the shared function
    final eventData = getNextEvent();

    return GestureDetector(
      onTap: onTap,
      child: IntrinsicHeight(
        child: Container(
          width: double.infinity,
          decoration: BoxDecoration(
            borderRadius: BorderRadius.circular(14),
            color: Colors.white,
          ),
          child: Padding(
            padding: const EdgeInsets.only(top: 10, left: 20, bottom: 10),
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              mainAxisSize:
                  MainAxisSize
                      .min, // Makes the column take only necessary space
              children: [
                Text(
                  'මීළඟ නැකත: ${eventData.name}',
                  style: const TextStyle(
                    fontSize: 30,
                    fontWeight: FontWeight.w500,
                    color: Colors.black,
                    fontFamily: 'UNIndeewaree',
                  ),
                ),
                const SizedBox(height: 12),

                Row(
                  children: [
                    const Text(
                      'දින:',
                      style: TextStyle(
                        fontSize: 25,
                        fontWeight: FontWeight.w500,
                        color: Colors.black,
                        fontFamily: 'UNIndeewaree',
                      ),
                    ),
                    const SizedBox(width: 2),
                    _buildCountdownBox(days),
                    const SizedBox(width: 6),

                    const Text(
                      'පැය:',
                      style: TextStyle(
                        fontSize: 25,
                        fontWeight: FontWeight.w500,
                        color: Colors.black,
                        fontFamily: 'UNIndeewaree',
                      ),
                    ),
                    const SizedBox(width: 2),
                    _buildCountdownBox(hours),
                    const SizedBox(width: 6),

                    const Text(
                      'මිනි:',
                      style: TextStyle(
                        fontSize: 25,
                        fontWeight: FontWeight.w500,
                        color: Colors.black,
                        fontFamily: 'UNIndeewaree',
                      ),
                    ),
                    const SizedBox(width: 2),
                    _buildCountdownBox(minutes),
                    const SizedBox(width: 6),

                    const Text(
                      'තත්:',
                      style: TextStyle(
                        fontSize: 25,
                        fontWeight: FontWeight.w500,
                        color: Colors.black,
                        fontFamily: 'UNIndeewaree',
                      ),
                    ),
                    const SizedBox(width: 2),
                    _buildCountdownBox(seconds),
                    const SizedBox(width: 6),
                  ],
                ),
              ],
            ),
          ),
        ),
      ),
    );
  }

  /// Creates a styled container for displaying countdown numbers
  Widget _buildCountdownBox(String value) {
    return Container(
      height: 44,
      width: 44,
      decoration: BoxDecoration(
        borderRadius: BorderRadius.circular(14),
        color: const Color(0xffFFE3AE),
      ),
      child: Center(
        child: Text(
          value,
          style: const TextStyle(fontFamily: 'UNArundathee', fontSize: 25),
        ),
      ),
    );
  }
}
