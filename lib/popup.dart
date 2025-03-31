import 'package:flutter/material.dart';
import 'dart:async';
import 'data/data.dart';

class PopupDialog extends StatefulWidget {
  final DataModel data;
  final String countdownDays;
  final String countdownHours;
  final String countdownMinutes;
  final String countdownSeconds;

  const PopupDialog({
    super.key,
    required this.data,
    required this.countdownDays,
    required this.countdownHours,
    required this.countdownMinutes,
    required this.countdownSeconds,
  });

  @override
  State<PopupDialog> createState() => _PopupDialogState();
}

class _PopupDialogState extends State<PopupDialog> {
  late String days;
  late String hours;
  late String minutes;
  late String seconds;
  Timer? _timer;

  @override
  void initState() {
    super.initState();

    // Initialize with provided values
    days = widget.countdownDays;
    hours = widget.countdownHours;
    minutes = widget.countdownMinutes;
    seconds = widget.countdownSeconds;

    // Start real-time countdown within the popup
    _startRealTimeCountdown();
  }

  @override
  void dispose() {
    _timer?.cancel();
    super.dispose();
  }

  /// Starts a real-time countdown that updates every second
  void _startRealTimeCountdown() {
    // Parse the target date from the data model
    final targetDateTime = _parseDateTime(widget.data.date, widget.data.time);

    // Update immediately
    _updateCountdown(targetDateTime);

    // Update every second
    _timer = Timer.periodic(const Duration(seconds: 1), (timer) {
      _updateCountdown(targetDateTime);
    });
  }

  /// Parses date and time strings into DateTime object
  DateTime _parseDateTime(String dateStr, String timeStr) {
    // Parse date (format: YYYY-MM-DD)
    final dateParts = dateStr.split('-');
    final year = int.parse(dateParts[0]);
    final month = int.parse(dateParts[1]);
    final day = int.parse(dateParts[2]);

    // Parse time (format: HH:MM AM/PM)
    final isPM = timeStr.toLowerCase().contains('pm');
    final timeParts = timeStr
        .replaceAll(RegExp(r'[AP]M'), '')
        .trim()
        .split(':');
    var hour = int.parse(timeParts[0]);
    final minute = int.parse(timeParts[1]);

    // Convert to 24-hour format if needed
    if (isPM && hour < 12) {
      hour += 12;
    } else if (!isPM && hour == 12) {
      hour = 0;
    }

    return DateTime(year, month, day, hour, minute);
  }

  /// Updates the countdown values based on remaining time
  void _updateCountdown(DateTime targetDateTime) {
    final now = DateTime.now();

    // Calculate the difference
    final difference = targetDateTime.difference(now);

    // If the target date is in the past, stop countdown
    if (difference.isNegative) {
      _timer?.cancel();
      setState(() {
        days = "00";
        hours = "00";
        minutes = "00";
        seconds = "00";
      });
      return;
    }

    // Calculate time components
    final daysRemaining = difference.inDays;
    final hoursRemaining = difference.inHours % 24;
    final minutesRemaining = difference.inMinutes % 60;
    final secondsRemaining = difference.inSeconds % 60;

    // Format and update the state
    setState(() {
      days = daysRemaining.toString().padLeft(2, '0');
      hours = hoursRemaining.toString().padLeft(2, '0');
      minutes = minutesRemaining.toString().padLeft(2, '0');
      seconds = secondsRemaining.toString().padLeft(2, '0');
    });
  }

  @override
  Widget build(BuildContext context) {
    return Dialog(
      backgroundColor: Colors.transparent,
      insetPadding: const EdgeInsets.symmetric(horizontal: 20, vertical: 24),
      child: Container(
        constraints: BoxConstraints(
          maxWidth: 400,
          maxHeight: MediaQuery.of(context).size.height * 0.85,
        ),
        decoration: BoxDecoration(
          color: Colors.white,
          borderRadius: BorderRadius.circular(24),
          boxShadow: [
            BoxShadow(
              color: Colors.black.withOpacity(0.1),
              blurRadius: 12,
              offset: const Offset(0, 4),
            ),
          ],
        ),
        child: SingleChildScrollView(
          child: Column(
            mainAxisSize: MainAxisSize.min,
            children: [
              // Close button
              Align(
                alignment: Alignment.topRight,
                child: Padding(
                  padding: const EdgeInsets.all(12.0),
                  child: InkWell(
                    onTap: () => Navigator.of(context).pop(),
                    customBorder: const CircleBorder(),
                    child: Container(
                      padding: const EdgeInsets.all(8),
                      decoration: BoxDecoration(
                        color: const Color(0xFFFEE9C0),
                        shape: BoxShape.circle,
                        boxShadow: [
                          BoxShadow(
                            color: Colors.black.withOpacity(0.05),
                            blurRadius: 4,
                            offset: const Offset(0, 2),
                          ),
                        ],
                      ),
                      child: const Icon(
                        Icons.close,
                        color: Colors.black,
                        size: 20,
                      ),
                    ),
                  ),
                ),
              ),

              // Center image with background container
              Padding(
                padding: const EdgeInsets.symmetric(
                  horizontal: 24.0,
                  vertical: 16.0,
                ),
                child: Container(
                  padding: const EdgeInsets.all(16.0),
                  decoration: BoxDecoration(
                    color: Colors.white,
                    borderRadius: BorderRadius.circular(16),
                    boxShadow: [
                      BoxShadow(
                        color: Colors.black.withOpacity(0.05),
                        blurRadius: 4,
                        offset: const Offset(0, 2),
                      ),
                    ],
                  ),
                  child: Container(
                    decoration: BoxDecoration(
                      borderRadius: BorderRadius.circular(12),
                    ),
                    child: Image.asset(
                      widget.data.photo, // Use photo from DataModel by ID
                      width: 240,
                      height: 240,
                      fit: BoxFit.contain,
                      errorBuilder: (context, error, stackTrace) {
                        return Container(
                          width: 240,
                          height: 240,
                          decoration: BoxDecoration(
                            color: const Color(0xFFFFF1D6),
                            borderRadius: BorderRadius.circular(12),
                          ),
                          child: const Center(
                            child: Column(
                              mainAxisSize: MainAxisSize.min,
                              children: [
                                Icon(
                                  Icons.image_not_supported,
                                  color: Colors.red,
                                  size: 48,
                                ),
                                SizedBox(height: 8),
                                Text(
                                  'Image Not Found',
                                  style: TextStyle(color: Colors.red),
                                ),
                              ],
                            ),
                          ),
                        );
                      },
                    ),
                  ),
                ),
              ),

              // Title
              Padding(
                padding: const EdgeInsets.only(
                  left: 24.0,
                  right: 24.0,
                  top: 24.0,
                  bottom: 8.0,
                ),
                child: Text(
                  widget.data.name,
                  style: const TextStyle(
                    fontSize: 28,
                    fontWeight: FontWeight.w600,
                    color: Colors.black87,
                    fontFamily: 'UNIndeewaree',
                  ),
                  textAlign: TextAlign.center,
                ),
              ),

              // Date and time
              Padding(
                padding: const EdgeInsets.symmetric(horizontal: 24.0),
                child: Text(
                  "${widget.data.date} ${widget.data.time}",
                  style: const TextStyle(
                    fontSize: 18,
                    fontWeight: FontWeight.w500,
                    color: Colors.black54,
                    fontFamily: 'UNArundathee',
                  ),
                  textAlign: TextAlign.center,
                ),
              ),

              // Countdown row
              Container(
                margin: const EdgeInsets.symmetric(
                  horizontal: 24.0,
                  vertical: 16.0,
                ),
                padding: const EdgeInsets.symmetric(
                  vertical: 16.0,
                  horizontal: 8.0,
                ),
                decoration: BoxDecoration(
                  color: const Color(0xFFFFF9ED),
                  borderRadius: BorderRadius.circular(16),
                  boxShadow: [
                    BoxShadow(
                      color: Colors.black.withOpacity(0.05),
                      blurRadius: 4,
                      offset: const Offset(0, 2),
                    ),
                  ],
                ),
                child: Row(
                  mainAxisAlignment: MainAxisAlignment.spaceEvenly,
                  children: [
                    _buildCountdownItem("දින:", days),
                    _buildCountdownItem("පැය:", hours),
                    _buildCountdownItem("මිනි:", minutes),
                    _buildCountdownItem("තත්:", seconds),
                  ],
                ),
              ),

              // Description
              Padding(
                padding: const EdgeInsets.only(
                  left: 24.0,
                  right: 24.0,
                  top: 8.0,
                  bottom: 32.0,
                ),
                child: Text(
                  widget.data.description,
                  style: const TextStyle(
                    fontSize: 16,
                    fontWeight: FontWeight.w500,
                    color: Colors.black87,
                    height: 1.5,
                    fontFamily: 'UNGanganee',
                  ),
                  textAlign: TextAlign.center,
                ),
              ),
            ],
          ),
        ),
      ),
    );
  }

  Widget _buildCountdownItem(String label, String value) {
    return Column(
      children: [
        Text(
          label,
          style: const TextStyle(
            fontSize: 16,
            fontWeight: FontWeight.w500,
            color: Colors.black87,
            fontFamily: 'UNIndeewaree',
          ),
        ),
        const SizedBox(height: 8),
        Container(
          height: 54,
          width: 54,
          decoration: BoxDecoration(
            borderRadius: BorderRadius.circular(16),
            color: const Color(0xFFFFE3AE),
            boxShadow: [
              BoxShadow(
                color: Colors.black.withOpacity(0.05),
                blurRadius: 4,
                offset: const Offset(0, 2),
              ),
            ],
          ),
          child: Center(
            child: Text(
              value,
              style: const TextStyle(
                fontFamily: 'UNArundathee',
                fontSize: 26,
                fontWeight: FontWeight.bold,
              ),
            ),
          ),
        ),
      ],
    );
  }
}
