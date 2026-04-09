import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import 'providers/auth_provider.dart';
import 'screens/auth/login_screen.dart';
import 'screens/group/group_list_screen.dart';

void main() {
  runApp(
    MultiProvider(
      providers: [
        ChangeNotifierProvider(create: (_) => AuthProvider()),
      ],
      child: const GapApp(),
    ),
  );
}

class GapApp extends StatelessWidget {
  const GapApp({super.key});

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: 'Gap',
      theme: ThemeData(
        primarySwatch: Colors.deepPurple,
        useMaterial3: true,
        fontFamily: 'Roboto',
      ),
      home: Consumer<AuthProvider>(
        builder: (context, auth, _) {
          return auth.isAuthenticated 
            ? const GroupListScreen() 
            : const LoginScreen();
        },
      ),
    );
  }
}
