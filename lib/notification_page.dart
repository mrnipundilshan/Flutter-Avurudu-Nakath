import 'package:flutter/material.dart';

class NotificationPage extends StatelessWidget {
  final List<Map<String, String>> notifications = [
    {
      "name": "Event Reminder",
      "time": "10:30 AM",
      "description": "Don't forget the meeting at 11:00 AM.",
    },
    {
      "name": "System Update",
      "time": "11:45 AM",
      "description": "A new update is available for your app.",
    },
    {
      "name": "New Message",
      "time": "12:15 PM",
      "description": "You have received a new message from John.",
    },
  ];

  NotificationPage({super.key});

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text("Notifications"),
        backgroundColor: Colors.blueAccent,
      ),
      body: ListView.builder(
        padding: const EdgeInsets.all(10),
        itemCount: notifications.length,
        itemBuilder: (context, index) {
          final notification = notifications[index];
          return Card(
            shape: RoundedRectangleBorder(
              borderRadius: BorderRadius.circular(10),
            ),
            child: ListTile(
              title: Text(
                notification["name"]!,
                style: const TextStyle(
                  fontSize: 18,
                  fontWeight: FontWeight.bold,
                ),
              ),
              subtitle: Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  Text(
                    "Time: ${notification["time"]}",
                    style: const TextStyle(
                      fontSize: 14,
                      fontWeight: FontWeight.w500,
                    ),
                  ),
                  const SizedBox(height: 4),
                  Text(
                    notification["description"]!,
                    style: const TextStyle(fontSize: 14),
                  ),
                ],
              ),
              leading: const Icon(
                Icons.notifications,
                color: Colors.blueAccent,
              ),
              contentPadding: const EdgeInsets.all(10),
            ),
          );
        },
      ),
    );
  }
}
