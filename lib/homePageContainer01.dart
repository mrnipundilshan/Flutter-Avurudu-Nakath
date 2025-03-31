import 'package:flutter/material.dart';
import 'package:intl/intl.dart';
import 'data/data.dart';

class HomePageContainer01 extends StatelessWidget {
  final String days;
  final String hours;
  final String minutes;
  final String seconds;
  final VoidCallback onTap;

  const HomePageContainer01({
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
    // Get the next upcoming event
    final eventData = getNextEvent();

    return GestureDetector(
      onTap: onTap,
      child: Container(
        height: 270,
        width: double.infinity,
        decoration: BoxDecoration(
          borderRadius: BorderRadius.circular(14),
          color: Colors.white,
        ),
        child: Padding(
          padding: const EdgeInsets.only(top: 10, left: 20),
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.start,
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

              Padding(
                padding: const EdgeInsets.only(top: 20, right: 20),
                child: Text(
                  eventData.description,
                  style: const TextStyle(
                    fontSize: 20,
                    fontFamily: 'UNGurulugomi',
                  ),
                  textAlign: TextAlign.justify,
                  maxLines: 3,
                  overflow: TextOverflow.ellipsis,
                ),
              ),
            ],
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

/// Returns the next upcoming event based on current date and time
DataModel getNextEvent() {
  final now = DateTime.now();
  DataModel? nextEvent;
  Duration? shortestDuration;

  for (var event in dataList) {
    // Parse event date and time
    final eventDateTime = _parseEventDateTime(event.date, event.time);

    // Skip events that have already passed
    if (eventDateTime.isBefore(now)) continue;

    // Calculate time remaining until this event
    final duration = eventDateTime.difference(now);

    // If this is the first valid event we've found, or it's sooner than our previous candidate
    if (shortestDuration == null || duration < shortestDuration) {
      shortestDuration = duration;
      nextEvent = event;
    }
  }

  // If no future events found, return the first event (or implement a fallback logic)
  return nextEvent ?? dataList[0];
}

/// Helper function to parse date and time strings into DateTime
DateTime _parseEventDateTime(String date, String time) {
  // Parse the date (which is already in format YYYY-MM-DD)
  final dateParts = date.split('-');
  final year = int.parse(dateParts[0]);
  final month = int.parse(dateParts[1]);
  final day = int.parse(dateParts[2]);

  // Parse the time (format: HH:MM AM/PM)
  int hour = 0;
  int minute = 0;

  // Handle time format
  if (time.contains('AM') || time.contains('PM')) {
    final timeFormat = DateFormat('hh:mm a');
    final dateTime = timeFormat.parse(time);
    hour = dateTime.hour;
    minute = dateTime.minute;
  } else {
    // Handle 24-hour format
    final timeParts = time.split(':');
    hour = int.parse(timeParts[0]);
    minute = int.parse(timeParts[1]);
  }

  return DateTime(year, month, day, hour, minute);
}
