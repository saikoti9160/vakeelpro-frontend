import React, { useState } from 'react';
import { useSelector } from 'react-redux';
import { Drawer, List, ListItem, ListItemText, ListItemIcon, Button } from '@mui/material';
import { Dashboard, Business, Folder, People, Notifications, Storage } from '@mui/icons-material';
import { RootState } from '../store/store';
import { useNavigate } from 'react-router-dom';

const Sidebar: React.FC = () => {
  const [open, setOpen] = useState(false);
  const user = useSelector((state: RootState) => state.auth.user);
  const navigate = useNavigate();

  const featurePrivileges = user?.featurePrivileges || {};

  const hasModule = (module: string) => Object.keys(featurePrivileges).includes(module);

  const menuItems = [
    ...(hasModule('DASHBOARD')
      ? [{ text: 'Dashboard', icon: <Dashboard />, path: '/dashboard' }]
      : []),
    ...(user?.accountType === 'ORGANIZATION' && hasModule('COMPANY_PROFILE')
      ? [{ text: 'Company Profile', icon: <Business />, path: '/company-profile' }]
      : []),
    ...(hasModule('CASEMANAGEMENT')
      ? [{ text: 'Case Management', icon: <Folder />, path: '/case-management' }]
      : []),
    ...((user?.accountType === 'ORGANIZATION' || user?.roles.includes('SUPER_ADMIN')) && hasModule('STAFF_AND_ROLE_MANAGEMENT')
      ? [{ text: 'Staff & Role Management', icon: <People />, path: '/staff-roles' }]
      : []),
    ...(hasModule('NOTIFICATIONS')
      ? [{ text: 'Notifications', icon: <Notifications />, path: '/notifications' }]
      : []),
    ...(hasModule('MASTERDATAMANAGEMENT')
      ? [{ text: 'Master Data', icon: <Storage />, path: '/master-data' }]
      : []),
  ];

  return (
    <Drawer
      variant="permanent"
      sx={{
        width: open ? 240 : 60,
        flexShrink: 0,
        '& .MuiDrawer-paper': {
          width: open ? 240 : 60,
          transition: 'width 0.3s',
          overflowX: 'hidden',
        },
      }}
      onMouseEnter={() => setOpen(true)}
      onMouseLeave={() => setOpen(false)}
    >
      <List>
        {menuItems.map((item) => (
          <ListItem key={item.text} disablePadding>
            <Button
              fullWidth
              onClick={() => navigate(item.path)}
              sx={{
                justifyContent: open ? 'flex-start' : 'center',
                padding: '10px 16px',
                textTransform: 'none',
              }}
              startIcon={item.icon}
            >
              {open && item.text}
            </Button>
          </ListItem>
        ))}
      </List>
    </Drawer>
  );
};

export default Sidebar;
