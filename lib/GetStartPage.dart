import 'package:flutter/material.dart';
import 'dart:math' as math;

import 'HomePage.dart';

class GetStartPage extends StatefulWidget {
  const GetStartPage({super.key});

  @override
  State<GetStartPage> createState() => _GetStartPageState();
}

class _GetStartPageState extends State<GetStartPage>
    with SingleTickerProviderStateMixin {
  // Animation controller for the sun
  late final AnimationController _sunController = AnimationController(
    duration: const Duration(seconds: 10),
    vsync: this,
  );

  late final Animation<double> _sunRotation;

  // For button animations
  final ButtonAnimationController _buttonController1 =
      ButtonAnimationController();
  final ButtonAnimationController _buttonController2 =
      ButtonAnimationController();

  // For decorative elements animation
  bool _showDecorations = false;

  // For page fade-in effect
  double _pageOpacity = 0.0;

  @override
  void initState() {
    super.initState();

    // Start the sun animation
    _sunController.repeat();

    // Create sun rotation animation
    _sunRotation = Tween<double>(
      begin: 0,
      end: 2 * math.pi,
    ).animate(_sunController);

    // Create page fade-in effect
    Future.delayed(const Duration(milliseconds: 100), () {
      if (mounted) {
        setState(() {
          _pageOpacity = 1.0;
        });
      }
    });

    // Show decorative elements after a delay
    Future.delayed(const Duration(milliseconds: 500), () {
      if (mounted) {
        setState(() {
          _showDecorations = true;
        });
      }
    });
  }

  @override
  void dispose() {
    _sunController.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: const Color(0xffFFBE45),
      body: AnimatedOpacity(
        opacity: _pageOpacity,
        duration: const Duration(milliseconds: 800),
        curve: Curves.easeIn,
        child: Center(
          child: Stack(
            alignment: Alignment.center,
            children: [
              // Decorative background gradient
              Container(
                width: MediaQuery.of(context).size.width,
                height: MediaQuery.of(context).size.height,
                decoration: const BoxDecoration(
                  gradient: RadialGradient(
                    center: Alignment.topCenter,
                    radius: 1.0,
                    colors: [Color(0xffFFD485), Color(0xffFFBE45)],
                  ),
                ),
              ),

              // Background image 1 positioned at top right
              Positioned(
                top: -40,
                left: -120,
                child: Opacity(
                  opacity: 0.5,
                  child: Image.asset(
                    'assets/bg1.png',
                    width: MediaQuery.of(context).size.width * 0.9,
                    alignment: Alignment.topRight,
                    fit: BoxFit.contain,
                  ),
                ),
              ),

              // Background image 2 positioned at bottom left
              Positioned(
                bottom: 150,
                right: -80,
                child: Opacity(
                  opacity: 0.5,
                  child: Image.asset(
                    'assets/bg2.png',
                    width: MediaQuery.of(context).size.width * 0.7,
                    alignment: Alignment.bottomLeft,
                    fit: BoxFit.contain,
                  ),
                ),
              ),

              // Background image 3 positioned at bottom
              Positioned(
                bottom: 0,
                left: 0,
                right: 0,
                child: Opacity(
                  opacity: 1,
                  child: Image.asset(
                    'assets/bg3.png',
                    width: MediaQuery.of(context).size.width,
                    fit: BoxFit.fitWidth,
                  ),
                ),
              ),

              // Animated decorative elements
              Positioned(
                bottom: 20,
                right: 20,
                child: AnimatedOpacity(
                  opacity: _showDecorations ? 1.0 : 0.0,
                  duration: const Duration(seconds: 1),
                  curve: Curves.easeIn,
                  child: AnimatedContainer(
                    duration: const Duration(seconds: 1),
                    width: 100,
                    height: 100,
                    margin: EdgeInsets.only(top: _showDecorations ? 0 : 20),
                    decoration: BoxDecoration(
                      shape: BoxShape.circle,
                      color: Colors.white.withOpacity(0.1),
                    ),
                  ),
                ),
              ),

              Positioned(
                bottom: 50,
                left: 30,
                child: AnimatedOpacity(
                  opacity: _showDecorations ? 1.0 : 0.0,
                  duration: const Duration(seconds: 1),
                  curve: Curves.easeIn,
                  child: AnimatedContainer(
                    duration: const Duration(seconds: 1),
                    width: 70,
                    height: 70,
                    margin: EdgeInsets.only(top: _showDecorations ? 0 : 20),
                    decoration: BoxDecoration(
                      shape: BoxShape.circle,
                      color: Colors.white.withOpacity(0.1),
                    ),
                  ),
                ),
              ),

              // Animated sun
              Positioned(
                top: 90,
                child: AnimatedBuilder(
                  animation: _sunController,
                  builder: (context, child) {
                    // Calculate oscillating scale value for "breathing" effect
                    double breatheScale =
                        1.0 +
                        0.05 * math.sin(_sunController.value * math.pi * 2);

                    return Transform.rotate(
                      angle: _sunRotation.value,
                      child: Transform.scale(
                        scale: breatheScale,
                        child: SizedBox(
                          width: 230,
                          height: 230,
                          child: Image.asset(
                            'assets/sun.png',
                            fit: BoxFit.cover,
                          ),
                        ),
                      ),
                    );
                  },
                ),
              ),

              Positioned(
                top: 90,
                child: SizedBox(
                  width: 230,
                  height: 230,
                  child: Image.asset('assets/sunFace.png', fit: BoxFit.cover),
                ),
              ),

              //main text set start - preserved as requested
              const Positioned(
                top: 330,
                left: 120,
                child: Text(
                  'අපේ',
                  style: TextStyle(
                    fontSize: 80,
                    fontWeight: FontWeight.w500,
                    color: Colors.black,
                    fontFamily: 'UNDisapamok',
                  ),
                ),
              ),
              const Positioned(
                top: 355,
                left: 185,
                child: Text(
                  'අවුරුදු',
                  style: TextStyle(
                    fontSize: 80,
                    fontWeight: FontWeight.w500,
                    color: Colors.black,
                    fontFamily: 'UNDisapamok',
                  ),
                ),
              ),
              const Positioned(
                top: 350,
                child: Text(
                  'නැකැත්',
                  style: TextStyle(
                    fontSize: 160,
                    fontWeight: FontWeight.w500,
                    color: Colors.black,
                    fontFamily: 'UNDisapamok',
                  ),
                ),
              ),
              const Positioned(
                top: 548,
                child: Text(
                  'புத்தாண்டு   வாழ்த்துக்கள்',
                  style: TextStyle(
                    fontSize: 14,
                    fontWeight: FontWeight.w800,
                    color: Colors.black,
                    fontFamily: 'UNDisapamok',
                    letterSpacing: 1.3,
                  ),
                ),
              ),
              //main text set end
              // Improved button with animations
              Positioned(
                top: 630,
                child: AnimatedLanguageButton(
                  text: 'සිංහල',
                  controller: _buttonController1,
                  onTap: () {
                    Navigator.of(context).push(
                      PageRouteBuilder(
                        transitionDuration: Duration(
                          milliseconds: 500,
                        ), // Animation speed
                        pageBuilder:
                            (context, animation, secondaryAnimation) =>
                                HomePage(),
                        transitionsBuilder: (
                          context,
                          animation,
                          secondaryAnimation,
                          child,
                        ) {
                          return FadeTransition(
                            // Use fade effect
                            opacity: animation,
                            child: child,
                          );
                        },
                      ),
                    );
                  },
                ),
              ),

              Positioned(
                top: 694,
                child: AnimatedLanguageButton(
                  text: 'தமிழ்',
                  controller: _buttonController2,
                  onTap: () {
                    // Add your navigation or language selection logic here
                  },
                ),
              ),
            ],
          ),
        ),
      ),
    );
  }
}

// Custom button animation controller
class ButtonAnimationController extends ChangeNotifier {
  bool _isHovering = false;
  bool _isPressed = false;

  bool get isHovering => _isHovering;
  bool get isPressed => _isPressed;

  void setHovering(bool value) {
    if (_isHovering != value) {
      _isHovering = value;
      notifyListeners();
    }
  }

  void setPressed(bool value) {
    if (_isPressed != value) {
      _isPressed = value;
      notifyListeners();
    }
  }
}

// Custom animated button widget
class AnimatedLanguageButton extends StatefulWidget {
  final String text;
  final ButtonAnimationController controller;
  final VoidCallback onTap;

  const AnimatedLanguageButton({
    super.key,
    required this.text,
    required this.controller,
    required this.onTap,
  });

  @override
  State<AnimatedLanguageButton> createState() => _AnimatedLanguageButtonState();
}

class _AnimatedLanguageButtonState extends State<AnimatedLanguageButton> {
  @override
  Widget build(BuildContext context) {
    return MouseRegion(
      onEnter: (_) => widget.controller.setHovering(true),
      onExit: (_) => widget.controller.setHovering(false),
      cursor: SystemMouseCursors.click,
      child: GestureDetector(
        onTapDown: (_) => widget.controller.setPressed(true),
        onTapUp: (_) => widget.controller.setPressed(false),
        onTapCancel: () => widget.controller.setPressed(false),
        onTap: widget.onTap,
        child: ListenableBuilder(
          listenable: widget.controller,
          builder: (context, _) {
            final isHovering = widget.controller.isHovering;
            final isPressed = widget.controller.isPressed;

            return AnimatedContainer(
              duration: const Duration(milliseconds: 150),
              height: 50,
              width: 300,
              margin: EdgeInsets.only(top: isPressed ? 2 : 0),
              decoration: BoxDecoration(
                borderRadius: BorderRadius.circular(14),
                color:
                    isPressed
                        ? Colors.white.withOpacity(0.8)
                        : isHovering
                        ? Colors.white.withOpacity(0.95)
                        : Colors.white,
                boxShadow:
                    isPressed
                        ? []
                        : isHovering
                        ? [
                          BoxShadow(
                            color: Colors.black.withOpacity(0.2),
                            blurRadius: 10,
                            offset: const Offset(0, 5),
                          ),
                        ]
                        : [],
              ),
              child: Center(
                child: AnimatedDefaultTextStyle(
                  duration: const Duration(milliseconds: 150),
                  style: TextStyle(
                    color: Colors.black,
                    fontSize: 25,
                    fontWeight:
                        isHovering ? FontWeight.bold : FontWeight.normal,
                  ),
                  child: Text(widget.text),
                ),
              ),
            );
          },
        ),
      ),
    );
  }
}
