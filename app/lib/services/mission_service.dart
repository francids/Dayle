import "dart:convert";

import "package:http/http.dart" as http;

class MissionService {
  static const _baseUrl = "https://dayle.francids.workers.dev";

  Future<String> fetchMission(List<String> previousMissions) async {
    final uri = Uri.parse("$_baseUrl/mission");
    final body = jsonEncode({"previousMissions": previousMissions});

    final response = await http
        .post(
          uri,
          headers: {"Content-Type": "application/json; charset=UTF-8"},
          body: body,
        )
        .timeout(const Duration(seconds: 30));

    if (response.statusCode < 200 || response.statusCode >= 300) {
      throw MissionServiceException(
        "HTTP ${response.statusCode}: ${response.body}",
      );
    }

    final parsed = jsonDecode(response.body) as Map<String, dynamic>;
    final mission = (parsed["mission"] as String).trim();
    if (mission.isEmpty) {
      throw MissionServiceException("Empty mission in response");
    }
    return mission;
  }
}

class MissionServiceException implements Exception {
  final String message;
  MissionServiceException(this.message);

  @override
  String toString() => message;
}
