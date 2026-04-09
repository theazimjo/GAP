import 'package:flutter/material.dart';
import 'package:flutter_secure_storage/flutter_secure_storage.dart';
import '../core/constants/constants.dart';
import '../core/network/api_client.dart';
import 'package:url_launcher/url_launcher.dart';

class AuthProvider with ChangeNotifier {
  final ApiClient _apiClient = ApiClient();
  final FlutterSecureStorage _storage = const FlutterSecureStorage();
  
  bool _isLoading = false;
  String? _token;
  Map<String, dynamic>? _user;

  bool get isLoading => _isLoading;
  bool get isAuthenticated => _token != null;
  Map<String, dynamic>? get user => _user;

  AuthProvider() {
    _loadToken();
  }

  Future<void> _loadToken() async {
    _token = await _storage.read(key: AppConstants.tokenKey);
    notifyListeners();
  }

  Future<bool> login(String phone, String password) async {
    _isLoading = true;
    notifyListeners();

    try {
      final response = await _apiClient.post('/auth/login', data: {
        'phone': phone,
        'password': password,
      });

      if (response.statusCode == 200) {
        _token = response.data['token'];
        _user = response.data['user'];
        await _storage.write(key: AppConstants.tokenKey, value: _token);
        _isLoading = false;
        notifyListeners();
        return true;
      }
    } catch (e) {
      print('Login error: $e');
    }

    _isLoading = false;
    notifyListeners();
    return false;
  }

  Future<bool> register(String name, String phone, String password) async {
    _isLoading = true;
    notifyListeners();

    try {
      final response = await _apiClient.post('/auth/register', data: {
        'name': name,
        'phone': phone,
        'password': password,
      });

      if (response.statusCode == 201) {
        _token = response.data['token'];
        _user = response.data['user'];
        await _storage.write(key: AppConstants.tokenKey, value: _token);
        _isLoading = false;
        notifyListeners();
        return true;
      }
    } catch (e) {
      print('Register error: $e');
    }

    _isLoading = false;
    notifyListeners();
    return false;
  }

  Future<String?> requestOtp(String phone) async {
    _isLoading = true;
    notifyListeners();

    try {
      final response = await _apiClient.post('/auth/request-otp', data: {
        'phone': phone,
      });

      if (response.statusCode == 200) {
        final otpCode = response.data['otpCode'];
        
        // Simulation: Send SMS to self
        final Uri smsUri = Uri(
          scheme: 'sms',
          path: phone,
          queryParameters: <String, String>{
            'body': 'Sizning GAP tasdiqlash kodingiz: $otpCode',
          },
        );
        
        if (await canLaunchUrl(smsUri)) {
          await launchUrl(smsUri);
        }
        
        _isLoading = false;
        notifyListeners();
        return otpCode;
      }
    } catch (e) {
      print('Request OTP error: $e');
    }

    _isLoading = false;
    notifyListeners();
    return null;
  }

  Future<bool> verifyOtp(String phone, String code) async {
    _isLoading = true;
    notifyListeners();

    try {
      final response = await _apiClient.post('/auth/verify-otp', data: {
        'phone': phone,
        'otpCode': code,
      });

      if (response.statusCode == 200) {
        _token = response.data['token'];
        _user = response.data['user'];
        await _storage.write(key: AppConstants.tokenKey, value: _token);
        _isLoading = false;
        notifyListeners();
        return true;
      }
    } catch (e) {
      print('Verify OTP error: $e');
    }

    _isLoading = false;
    notifyListeners();
    return false;
  }

  Future<void> logout() async {
    _token = null;
    _user = null;
    await _storage.delete(key: AppConstants.tokenKey);
    notifyListeners();
  }
}
