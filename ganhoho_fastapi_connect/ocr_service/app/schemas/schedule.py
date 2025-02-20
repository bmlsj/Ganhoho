from pydantic import BaseModel
from typing import List

class DaySchedule(BaseModel):
    day: int
    type: str

class NurseSchedule(BaseModel):
    name: str
    year: int
    month: int
    schedule: List[DaySchedule]

class ScheduleResponse(BaseModel):
    data: List[NurseSchedule]