from typing import List, Dict
from datetime import datetime

class ScheduleBuilder:
    def __init__(self):
        now = datetime.now()
        self.current_year = now.year
        self.current_month = now.month

    def build_schedule(self, schedule_data: List[Dict]) -> Dict:
        result = []
        for nurse in schedule_data:
            schedule = nurse["schedule"]
            result.append({
                "name": nurse["name"],
                "year": self.current_year,
                "month": self.current_month,
                "schedule": schedule 
            })
        
        return {"data": result}