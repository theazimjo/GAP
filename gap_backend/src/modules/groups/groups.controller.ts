import { Controller, Get, Post, Body, Param, UseGuards, Req } from '@nestjs/common';
import { GroupsService } from './groups.service';
import { JwtAuthGuard } from '../auth/guards/jwt-auth.guard';

@Controller('api/groups')
@UseGuards(JwtAuthGuard)
export class GroupsController {
  constructor(private readonly groupsService: GroupsService) {}

  @Post()
  async create(@Body() body: any, @Req() req: any) {
    return this.groupsService.createGroup(
      body.name,
      body.totalPool,
      body.contributionAmount,
      req.user.userId,
    );
  }

  @Get()
  async findAll(@Req() req: any) {
    return this.groupsService.getGroups(req.user.userId);
  }

  @Get(':id')
  async findOne(@Param('id') id: string) {
    return this.groupsService.getGroupDetail(+id);
  }

  @Post('add-member')
  async addMember(@Body() body: any, @Req() req: any) {
    return this.groupsService.addMember(
      body.groupId,
      body.phone,
      req.user.userId,
    );
  }
}
