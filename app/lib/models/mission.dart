class Mission {
  final int id;
  final String date;
  final String text;
  final bool completed;

  Mission({
    required this.id,
    required this.date,
    required this.text,
    required this.completed,
  });

  Map<String, dynamic> toMap() {
    return {
      "id": id,
      "date": date,
      "text": text,
      "completed": completed ? 1 : 0,
    };
  }

  factory Mission.fromMap(Map<String, dynamic> map) {
    return Mission(
      id: map["id"] as int,
      date: map["date"] as String,
      text: map["text"] as String,
      completed: (map["completed"] as int) == 1,
    );
  }
}
