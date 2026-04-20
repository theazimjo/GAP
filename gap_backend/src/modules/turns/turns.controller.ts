import { Controller, Get, Post, Body, Param, UseGuards, Req } from '@nestjs/common';
import { TurnsService } from './turns.service';
import { JwtAuthGuard } from '../auth/guards/jwt-auth.guard';

@Controller('api')
@UseGuards(JwtAuthGuard)
export class TurnsController {
  constructor(private readonly turnsService: TurnsService) {}

  @Post('generate')
  async generate(@Body() body: any, @Req() req: any) {
    return this.turnsService.generateTurns(
      body.groupId,
      body.startDate,
      body.frequencyDays,
      req.user.userId,
    );
  }

  @Get(':groupId')
  async findByGroup(@Param('groupId') groupId: string) {
    return this.turnsService.getTurns(+groupId);
  }
}
