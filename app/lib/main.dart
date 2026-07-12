import "package:flutter/material.dart";

void main() {
  runApp(const DayleApp());
}

class DayleApp extends StatelessWidget {
  const DayleApp({super.key});

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: "Dayle",
      debugShowCheckedModeBanner: false,
      home: Scaffold(body: Center(child: Text("Dayle"))),
    );
  }
}
