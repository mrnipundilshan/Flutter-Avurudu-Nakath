import 'package:flutter/material.dart';
import 'dart:async';

import 'data/data.dart';
import 'homePageContainer01.dart';
import 'homePageContainer02.dart';
import 'popup.dart';
import 'notification_service.dart';

class HomePage extends StatefulWidget {
  const HomePage({super.key});

  @override
  State<HomePage> createState() => _HomePageState();
}

class _HomePageState extends State<HomePage> {
  final ScrollController _scrollController = ScrollController();
  bool _isScrolled = false;
  double _scrollProgress = 0.0;

  final double _titleHeight = 160.0;
  final double _container02Height = 130.0;
  final double _topPadding = 60.0;
  final double _scrollThreshold = 0;
  final double _maxTransitionOffset = 200.0;

  // Timer for countdown
  Timer? _countdownTimer;
  Timer? _eventCheckTimer;

  // Current countdown values
  String days = "00";
  String hours = "00";
  String minutes = "00";
  String seconds = "00";

  // ID of the next event (will be determined dynamically)
  int nextEventId = 1; // Default value
  late DataModel nextEvent;

  @override
  void initState() {
    super.initState();
    _scrollController.addListener(_scrollListener);

    // Find the next event
    _updateNextEvent();

    // Start the countdown timer
    _startCountdown();

    // Set up a timer to periodically check if the next event has changed
    _eventCheckTimer = Timer.periodic(const Duration(minutes: 1), (timer) {
      _updateNextEvent();
    });

    // Schedule notifications for all events
    _scheduleNotifications();
  }

  @override
  void dispose() {
    _scrollController.removeListener(_scrollListener);
    _scrollController.dispose();
    _countdownTimer?.cancel();
    _eventCheckTimer?.cancel();
    super.dispose();
  }

  void _scrollListener() {
    final double offset = _scrollController.offset;
    final double rawProgress =
        (offset - _scrollThreshold) / (_maxTransitionOffset - _scrollThreshold);
    final double progress = rawProgress.clamp(0.0, 1.0);
    final bool isNowScrolled = offset > _scrollThreshold;

    if (isNowScrolled != _isScrolled || progress != _scrollProgress) {
      setState(() {
        _isScrolled = isNowScrolled;
        _scrollProgress = progress;
      });
    }
  }

  /// Finds and updates the next upcoming event
  void _updateNextEvent() {
    final now = DateTime.now();
    DataModel? upcomingEvent;
    Duration? shortestDuration;

    for (var event in dataList) {
      final eventDateTime = _parseDateTime(event.date, event.time);
      if (eventDateTime.isBefore(now)) continue;

      final duration = eventDateTime.difference(now);
      if (shortestDuration == null || duration < shortestDuration) {
        shortestDuration = duration;
        upcomingEvent = event;
      }
    }

    if (upcomingEvent != null) {
      setState(() {
        nextEvent = upcomingEvent!;
        nextEventId = upcomingEvent.id;
      });
    }
  }

  /// Restarts the countdown timer
  void _restartCountdown() {
    _countdownTimer?.cancel();
    _startCountdown();
  }

  /// Starts the countdown timer that updates every second
  void _startCountdown() {
    // Get the target date for the next event
    final targetDateTime = _parseDateTime(nextEvent.date, nextEvent.time);

    // Update countdown immediately
    _updateCountdown(targetDateTime);

    // Set up timer to update countdown every second
    _countdownTimer = Timer.periodic(const Duration(seconds: 1), (timer) {
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

    // Parse time (format: HH:MM AM/PM or 24-hour format)
    int hour = 0;
    int minute = 0;

    if (timeStr.toLowerCase().contains('am') ||
        timeStr.toLowerCase().contains('pm')) {
      // 12-hour format with AM/PM
      final isPM = timeStr.toLowerCase().contains('pm');
      final timeParts = timeStr
          .replaceAll(RegExp(r'[AP]M'), '')
          .trim()
          .split(':');
      hour = int.parse(timeParts[0]);
      minute = int.parse(timeParts[1]);

      // Convert to 24-hour format if needed
      if (isPM && hour < 12) {
        hour += 12;
      } else if (!isPM && hour == 12) {
        hour = 0;
      }
    } else {
      // 24-hour format
      final timeParts = timeStr.split(':');
      hour = int.parse(timeParts[0]);
      minute = int.parse(timeParts[1]);
    }

    return DateTime(year, month, day, hour, minute);
  }

  /// Updates the countdown values based on remaining time
  void _updateCountdown(DateTime targetDateTime) {
    final now = DateTime.now();

    // Calculate the difference
    final difference = targetDateTime.difference(now);

    // If the target date is in the past, stop countdown and check for next event
    if (difference.isNegative) {
      _updateNextEvent();
      // Restart the countdown with the new next event
      _restartCountdown();
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
    return Scaffold(
      body: Stack(
        children: [
          // Background gradient
          Container(
            decoration: const BoxDecoration(
              gradient: RadialGradient(
                center: Alignment.topCenter,
                radius: 1.0,
                colors: [Color(0xffFFD485), Color(0xffFFBE45)],
              ),
            ),
          ),

          // Static background images
          _buildBackgroundImages(),

          // Main content
          SafeArea(
            child: CustomScrollView(
              controller: _scrollController,
              physics: const ClampingScrollPhysics(),
              slivers: [
                // Top padding
                SliverPadding(
                  padding: EdgeInsets.only(
                    top:
                        _titleHeight +
                        (_container02Height * _scrollProgress) +
                        20,
                  ),
                  sliver: const SliverToBoxAdapter(child: SizedBox()),
                ),

                // Main content containers
                _buildMainContent(),

                // Bottom padding
                const SliverToBoxAdapter(child: SizedBox(height: 40)),
              ],
            ),
          ),

          // Overlay elements (title and container02)
          _buildOverlayElements(),
        ],
      ),
    );
  }

  Widget _buildBackgroundImages() {
    return Stack(
      children: [
        Positioned(
          bottom: 0,
          left: 0,
          right: 0,
          child: Image.asset(
            'assets/bg3.png',
            width: double.infinity,
            fit: BoxFit.fitWidth,
          ),
        ),
        Positioned(
          top: -30,
          right: -30,
          child: Opacity(
            opacity: 0.6,
            child: Image.asset(
              'assets/sun.png',
              width: 150,
              height: 150,
              fit: BoxFit.cover,
            ),
          ),
        ),
        Positioned(
          top: -20,
          left: -150,
          child: Opacity(
            opacity: 0.5,
            child: Image.asset(
              'assets/bg1.png',
              width: MediaQuery.of(context).size.width * 1.2,
              fit: BoxFit.contain,
            ),
          ),
        ),
        Positioned(
          top: 440,
          right: -80,
          child: Opacity(
            opacity: 0.5,
            child: Image.asset(
              'assets/bg2.png',
              width: MediaQuery.of(context).size.width * 0.8,
              fit: BoxFit.contain,
            ),
          ),
        ),
      ],
    );
  }

  Widget _buildMainContent() {
    return SliverList(
      delegate: SliverChildListDelegate([
        // Container 01
        Opacity(
          opacity: 1.0 - _scrollProgress,
          child: Transform.translate(
            offset: Offset(0, -50 * _scrollProgress),
            child: Padding(
              padding: const EdgeInsets.symmetric(horizontal: 24),
              child: HomePageContainer01(
                days: days,
                hours: hours,
                minutes: minutes,
                seconds: seconds,
                eventId: nextEventId,
                onTap: () => _showPopup(context, nextEventId),
              ),
            ),
          ),
        ),

        // Line Art
        Padding(
          padding: const EdgeInsets.symmetric(vertical: 28, horizontal: 24),
          child: Image.asset(
            'assets/lineArt.png',
            width: double.infinity,
            fit: BoxFit.cover,
          ),
        ),

        // Event List
        Padding(
          padding: const EdgeInsets.fromLTRB(24, 0, 24, 32),
          child: _buildEventList(),
        ),
      ]),
    );
  }

  Widget _buildEventList() {
    return Container(
      decoration: BoxDecoration(
        borderRadius: BorderRadius.circular(16),
        color: Colors.white,
        boxShadow: [
          BoxShadow(
            color: Colors.black.withOpacity(0.05),
            blurRadius: 8,
            offset: const Offset(0, 2),
          ),
        ],
      ),
      child: ListView.builder(
        shrinkWrap: true,
        physics: const NeverScrollableScrollPhysics(),
        itemCount: dataList.length,
        itemBuilder: (context, index) => _buildEventItem(dataList[index]),
      ),
    );
  }

  Widget _buildEventItem(DataModel event) {
    return GestureDetector(
      onTap: () => _showPopup(context, event.id),
      child: Container(
        margin: const EdgeInsets.all(12),
        decoration: BoxDecoration(
          borderRadius: BorderRadius.circular(14),
          color: const Color(0xffFFF1D6),
        ),
        padding: const EdgeInsets.all(16.0),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Text(
              event.name,
              style: const TextStyle(
                fontSize: 22,
                fontWeight: FontWeight.w500,
                color: Colors.black,
                fontFamily: 'UNIndeewaree',
              ),
            ),
            const SizedBox(height: 10),
            Text(
              event.description,
              style: const TextStyle(
                fontSize: 16,
                fontWeight: FontWeight.w500,
                color: Colors.black,
                fontFamily: 'UNGanganee',
              ),
            ),
          ],
        ),
      ),
    );
  }

  Widget _buildOverlayElements() {
    return Stack(
      children: [
        // Title
        Positioned(
          top: 0,
          left: 0,
          right: 0,
          child: Opacity(
            opacity: 1.0 - _scrollProgress,
            child: const Padding(
              padding: EdgeInsets.fromLTRB(10, 70, 10, 0),
              child: Text(
                'සුභ අළුත් අවුරුද්දක් වේවා',
                style: TextStyle(
                  fontSize: 60,
                  fontWeight: FontWeight.w500,
                  color: Colors.black,
                  fontFamily: 'UNDisapamok',
                ),
                textAlign: TextAlign.center,
              ),
            ),
          ),
        ),

        // Container 02
        Positioned(
          top: _topPadding,
          left: 0,
          right: 0,
          child: Opacity(
            opacity: _scrollProgress,
            child: Padding(
              padding: const EdgeInsets.symmetric(horizontal: 24),
              child: Transform.translate(
                offset: Offset(0, (1.0 - _scrollProgress) * 30),
                child: HomePageContainer02(
                  days: days,
                  hours: hours,
                  minutes: minutes,
                  seconds: seconds,
                  eventId: nextEventId,
                  onTap: () => _showPopup(context, nextEventId),
                ),
              ),
            ),
          ),
        ),
      ],
    );
  }

  /// Shows a popup dialog with countdown information for the specified event
  void _showPopup(BuildContext context, int id) {
    final data = dataList.firstWhere((item) => item.id == id);

    // For the selected event, calculate its own countdown
    final targetDateTime = _parseDateTime(data.date, data.time);
    final now = DateTime.now();
    final difference = targetDateTime.difference(now);

    String eventDays = "00";
    String eventHours = "00";
    String eventMinutes = "00";
    String eventSeconds = "00";

    // Only calculate if the event is in the future
    if (!difference.isNegative) {
      eventDays = difference.inDays.toString().padLeft(2, '0');
      eventHours = (difference.inHours % 24).toString().padLeft(2, '0');
      eventMinutes = (difference.inMinutes % 60).toString().padLeft(2, '0');
      eventSeconds = (difference.inSeconds % 60).toString().padLeft(2, '0');
    }

    showDialog(
      context: context,
      builder: (BuildContext context) {
        return PopupDialog(
          data: data,
          countdownDays: eventDays,
          countdownHours: eventHours,
          countdownMinutes: eventMinutes,
          countdownSeconds: eventSeconds,
        );
      },
    );
  }

  // Schedule notifications for all events
  Future<void> _scheduleNotifications() async {
    await NotificationService.scheduleAllNotifications();
  }
}
